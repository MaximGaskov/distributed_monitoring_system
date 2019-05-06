
CREATE OR REPLACE FUNCTION
        monitoring_schema.check_self_monitoring()
        RETURNS trigger AS
$$
DECLARE
randomHostId integer;
workingMHostsNumber integer;
hostWithNullAnotherMhIP integer;
BEGIN

        IF NEW.is_up = FALSE THEN

                IF EXISTS (SELECT * FROM monitoring_schema.monitoring_hosts WHERE another_mh_ip_addr IS NULL AND is_up = TRUE) THEN
                        hostWithNullAnotherMhIP = (SELECT id FROM monitoring_schema.monitoring_hosts 
                                WHERE another_mh_ip_addr IS NULL AND is_up = TRUE);
                        UPDATE monitoring_schema.monitoring_hosts SET another_mh_ip_addr = NEW.ip_addr WHERE id = hostWithNullAnotherMhIP;
                END IF;
        ELSE

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
        END IF;

RETURN NEW;
END;
$$ 
LANGUAGE 'plpgsql';

CREATE TRIGGER insertToMonitoringHosts
  BEFORE INSERT
  ON monitoring_schema.monitoring_hosts
  FOR EACH ROW
  EXECUTE PROCEDURE monitoring_schema.check_self_monitoring();

