CREATE OR REPLACE FUNCTION 
        monitoring_schema.redistribute_target_host_on_mHost_deletion ()
        RETURNS trigger AS
$$
DECLARE
mhID integer;
updChunkSize integer;
numberOfTargets integer;
numberOfMHostsLeft integer;

BEGIN

-- Redistribute targets

UPDATE monitoring_schema.hosts SET observer_id = NULL WHERE observer_id = OLD.id;

numberOfTargets := COUNT(*) FROM monitoring_schema.hosts WHERE observer_id IS NULL;
numberOfMHostsLeft := COUNT(*) FROM monitoring_schema.monitoring_hosts WHERE id <> OLD.id AND is_up = TRUE;

        -- Reassign self monitoring
        IF numberOfMHostsLeft = 1 THEN
                UPDATE monitoring_schema.monitoring_hosts SET another_mh_ip_addr = NULL
                        WHERE ip_addr = OLD.another_mh_ip_addr;
        ELSE
                UPDATE monitoring_schema.monitoring_hosts SET another_mh_ip_addr = OLD.another_mh_ip_addr
                        WHERE another_mh_ip_addr = OLD.ip_addr;
        END IF;
        ------------

IF numberOfMHostsLeft = 0 THEN
        RETURN OLD;
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

RETURN OLD;
END IF;
END;
$$

LANGUAGE 'plpgsql';

CREATE TRIGGER redistribute_targets_on_MH_delete_trigger
   AFTER DELETE ON monitoring_schema.monitoring_hosts FOR EACH ROW
   EXECUTE PROCEDURE monitoring_schema.redistribute_target_host_on_mHost_deletion();


