package ytuce.gp.mfmsp.Service.ExternalService.Impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ytuce.gp.mfmsp.Constants.Platform;
import ytuce.gp.mfmsp.Service.ExternalService.ApplicationBridgeService;
import ytuce.gp.mfmsp.Service.ExternalService.ExternalService;

import java.util.List;



@Service
@Log4j2
@AllArgsConstructor
public class ApplicationBridgeServiceImpl implements ApplicationBridgeService {
    @Autowired
    private List<ExternalService> externalServices;


    @Autowired
    private FacebookServiceImpl facebookService;

    @Autowired
    private InstagramServiceImpl instagramService;

    @Autowired
    private TwitterServiceImpl twitterService;
    @Autowired
    private TelegramServiceImpl telegramService;

    @Transactional
    public void getAllConversations(){
        externalServices.forEach(ExternalService::receiveMessages);
    }

    public List<ExternalService>  getAllExternalServices(){
        return externalServices;
    }

    public ExternalService getExternalServiceByPlatformName(Platform platform){
        ExternalService externalService = null;
        switch (platform) {
            case FACEBOOK:
                externalService = facebookService;
                break;
            case TWITTER:
                externalService = twitterService;
                break;
            case INSTAGRAM:
                externalService = instagramService;
                break;
            case TELEGRAM:
                externalService = telegramService;
                break;
            default:
                break;
        }
        return externalService;
    }
}
