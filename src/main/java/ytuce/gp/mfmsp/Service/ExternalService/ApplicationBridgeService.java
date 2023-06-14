package ytuce.gp.mfmsp.Service.ExternalService;

import ytuce.gp.mfmsp.Constants.Platform;

import java.util.List;

public interface ApplicationBridgeService {
    void getAllConversations();
    List<ExternalService> getAllExternalServices();

    ExternalService getExternalServiceByPlatformName(Platform platform);
}
