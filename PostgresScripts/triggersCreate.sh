#!/bin/bash
psql monsys_db -f createAddNewHostTrigger.sql
psql monsys_db -f createAddNewMonitoringHostTrigger.sql
psql monsys_db -f createRedistributionTriggerOnMhDelete.sql
psql monsys_db -f createRedistributionTriggerOnMhUpdate.sql
