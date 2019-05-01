package edu.max.monsys.monitoring;

import edu.max.monsys.entity.MonitoringHost;
import edu.max.monsys.repository.MonitoringHostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClusterSelfMonitoringHandler {

    @Autowired
    MonitoringHostRepository monitoringHostRepository;

    public void reassignClusterSelfMonitoringOnMHostAdding(Integer mhostID) {
        if (monitoringHostRepository.count() == 2) {

            Optional<MonitoringHost> addedMHost = monitoringHostRepository.findById(mhostID);


            for (MonitoringHost mh : monitoringHostRepository.findAll()) {
                if (!mh.getIpAddress().equals(addedMHost.get().getIpAddress())){
                    mh.setAnotherMHIpAdress(addedMHost.get().getIpAddress());
                    addedMHost.get().setAnotherMHIpAdress(mh.getIpAddress());
                    break;
                }
            }

        }
        else {

            MonitoringHost addedMHost = monitoringHostRepository.findById(mhostID).get();
            MonitoringHost randomMHostPicked = null;
            for (MonitoringHost mh : monitoringHostRepository.findAll()) {
                if (!mh.getIpAddress().equals(addedMHost.getIpAddress())) {
                    randomMHostPicked = mh;
                    break;
                }
            }

            if (randomMHostPicked != null) {
                String randMHanotherIp = randomMHostPicked.getAnotherMHIpAdress();
                randomMHostPicked.setAnotherMHIpAdress(addedMHost.getIpAddress());
                addedMHost.setAnotherMHIpAdress(randMHanotherIp);
            } else
                throw new NullPointerException();

        }


        monitoringHostRepository.flush();
    }


    public void reassignClusterSelfMonitoringOnMHostDeletion(Integer mhostID) {

        Optional<MonitoringHost> mhToDelete = monitoringHostRepository.findById(mhostID);
        monitoringHostRepository.findMonitoringHostByAnotherMHIpAdress(mhToDelete.get().getIpAddress())
                .get().setAnotherMHIpAdress(mhToDelete.get().getAnotherMHIpAdress());

        monitoringHostRepository.flush();
    }
}
