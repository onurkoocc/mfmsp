package ytuce.gp.mfmsp.Service.ExternalService.Impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ytuce.gp.mfmsp.Service.ExternalService.ApplicationBridgeService;
import ytuce.gp.mfmsp.Service.ExternalService.ExternalService;

import java.util.List;



@Service
@Log4j2
@AllArgsConstructor
public class ApplicationBridgeServiceImpl implements ApplicationBridgeService {
    @Autowired
    private List<ExternalService> externalServices;
    @Transactional
    public void getAllConversations(){

        externalServices.forEach(ExternalService::receiveMessages);
    }
}
