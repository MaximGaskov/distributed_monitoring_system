CREATE OR REPLACE FUNCTION 
        monitoring_schema.redistribute_target_host_on_mHost_update ()
        RETURNS trigger AS
$$
DECLARE
mhID integer;
busiestID integer;
updChunkSize integer;
randomHostId integer;
numberOfTargets integer;
busiestTargetNum integer;
numberOfMHostsLeft integer;
workingMHostsNumber integer;
BEGIN

IF (OLD.is_up = TRUE) AND (NEW.is_up = FALSE) 
    THEN

--Redistributr targets
        UPDATE monitoring_schema.hosts SET observer_id = NULL WHERE observer_id = OLD.id;

        numberOfTargets := COUNT(*) FROM monitoring_schema.hosts WHERE observer_id IS NULL;
        numberOfMHostsLeft := COUNT(*) FROM monitoring_schema.monitoring_hosts WHERE id <> OLD.id AND is_up = TRUE;

        -- Reassign self monitoring
        IF numberOfMHostsLeft = 1 THEN
                NEW.another_mh_ip_addr = NULL;
        ELSE
                UPDATE monitoring_schema.monitoring_hosts SET another_mh_ip_addr = OLD.another_mh_ip_addr
                        WHERE another_mh_ip_addr = OLD.ip_addr;
        END IF;
        ------------

        IF numberOfMHostsLeft = 0 THEN 
        RETURN NEW;
        ELSE

        updChunkSize = numberOfTargets / numberOfMHostsLeft;

        FOR mhID IN (SELECT id FROM monitoring_schema.monitoring_hosts
            WHERE id <> OLD.id) 
        LOOP
            UPDATE monitoring_schema.hosts SET observer_id = mhID WHERE id IN
                (SELECT id FROM monitoring_schema.hosts WHERE observer_id IS NULL LIMIT updChunkSize);
        END LOOP;

        -- update left hosts
        UPDATE monitoring_schema.hosts SET observer_id = 
                (SELECT id FROM monitoring_schema.monitoring_hosts WHERE id <> OLD.id AND is_up = TRUE LIMIT 1) 
                WHERE observer_id IS NULL;
        END IF;


ELSE IF (OLD.is_up = FALSE) AND (NEW.is_up = TRUE) 
    THEN


--Reassign self monitoring

        workingMHostsNumber := COUNT(*) FROM monitoring_schema.monitoring_hosts WHERE is_up = TRUE AND id <> NEW.id;
        randomHostId := (SELECT id FROM monitoring_schema.monitoring_hosts WHERE is_up = TRUE AND id <> NEW.id LIMIT 1);

        IF workingMHostsNumber > 1 THEN
                NEW.another_mh_ip_addr = (SELECT another_mh_ip_addr FROM monitoring_schema.monitoring_hosts
                        WHERE id = randomHostId);

                UPDATE monitoring_schema.monitoring_hosts SET another_mh_ip_addr = NEW.ip_addr
                        WHERE id = randomHostId;
        ELSE
                NEW.another_mh_ip_addr = (SELECT ip_addr FROM monitoring_schema.monitoring_hosts
                        WHERE id = randomHostId);
                UPDATE monitoring_schema.monitoring_hosts SET another_mh_ip_addr = NEW.ip_addr
                        WHERE id = randomHostId;
        END IF;

--Redistribute targets

        busiestID := (SELECT id FROM ( SELECT COUNT(*), monitoring_schema.monitoring_hosts.id 
                        FROM monitoring_schema.hosts INNER JOIN monitoring_schema.monitoring_hosts 
                        ON hosts.observer_id = monitoring_hosts.id 
                        GROUP BY monitoring_hosts.id
                        ORDER BY count DESC) AS foo
                  FETCH FIRST 1 ROWS ONLY);

        busiestTargetNum := COUNT(*) FROM monitoring_schema.hosts WHERE observer_id = busiestID;

        IF busiestTargetNum > 1 THEN
            UPDATE monitoring_schema.hosts SET observer_id = NEW.id WHERE id IN 
                (SELECT id FROM monitoring_schema.hosts WHERE observer_id = busiestID LIMIT (busiestTargetNum/2));
        END IF;
    END IF;
END IF;
RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER redistribute_targets_on_MH_update_trigger
   BEFORE UPDATE 
   ON monitoring_schema.monitoring_hosts 
   FOR EACH ROW
   EXECUTE PROCEDURE monitoring_schema.redistribute_target_host_on_mHost_update();


