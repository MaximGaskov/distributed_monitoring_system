CREATE OR REPLACE FUNCTION 
        monitoring_schema.add_host_to_laziest_func()
        RETURNS trigger AS
$$
BEGIN
NEW.observer_id = (SELECT id FROM monitoring_schema.monitoring_hosts 
                        WHERE NOT EXISTS (SELECT observer_id from monitoring_schema.hosts 
                                                WHERE  observer_id=monitoring_hosts.id) AND is_up <> FALSE
                   FETCH FIRST 1 ROWS ONLY);
IF NEW.observer_id IS NULL THEN
NEW.observer_id = (SELECT id FROM ( SELECT COUNT(*), monitoring_hosts.id 
                        FROM monitoring_schema.hosts INNER JOIN monitoring_schema.monitoring_hosts 
                        ON hosts.observer_id = monitoring_hosts.id 
                        GROUP BY monitoring_hosts.id
                        ORDER BY count) AS foo
                  FETCH FIRST 1 ROWS ONLY);
END IF;


RETURN NEW;
END;
$$ 
LANGUAGE 'plpgsql';

CREATE TRIGGER insertToHosts
  BEFORE INSERT
  ON monitoring_schema.hosts
  FOR EACH ROW
  EXECUTE PROCEDURE monitoring_schema.add_host_to_laziest_func();
