package ytuce.gp.mfmsp.Service.ExternalService.Impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Message;
import ytuce.gp.mfmsp.Entity.Representative;
import ytuce.gp.mfmsp.Pojo.ConversationPojo;
import ytuce.gp.mfmsp.Pojo.RepresentativeStatisticsPojo;
import ytuce.gp.mfmsp.Repository.RepresentativeRepository;
import ytuce.gp.mfmsp.Service.ExternalService.AdminService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    @Autowired
    RepresentativeRepository representativeRepository;
    @Override
    public List<RepresentativeStatisticsPojo> getRepresentativeStatistics(){
        List<Representative> representativeList = representativeRepository.findAll();
        List<RepresentativeStatisticsPojo> representativeStatisticsPojoList = new ArrayList<>();
        for(Representative representative: representativeList){
            RepresentativeStatisticsPojo representativeStatisticsPojo = new RepresentativeStatisticsPojo();
            representativeStatisticsPojo.setRepresentativeName(representative.getEmail());
            representativeStatisticsPojo.setEndedConversationCount((int)representative.getConversationList()
                    .stream().filter(Conversation::getHasEnded).count());
            representativeStatisticsPojo.setOngoingConversationCount((int)representative.getConversationList()
                    .stream().filter(c -> !c.getHasEnded()).count());
            representativeStatisticsPojo.setTotalConversationCount(representative.getConversationList().size());
            representativeStatisticsPojo.setSentMessageCount(representative.getConversationList()
                    .stream().mapToInt(c->(int)c.getMessages()
                            .stream().filter(Message::getDirection).count()).sum());
            representativeStatisticsPojo.setReceivedMessageCount(representative.getConversationList()
                    .stream().mapToInt(c->(int)c.getMessages()
                            .stream().filter(m->!m.getDirection()).count()).sum());
            representativeStatisticsPojo.setTotalMessageCount(representative.getConversationList()
                    .stream().mapToInt(c->c.getMessages().size()).sum());
            representativeStatisticsPojo.setSentWordCount(representative.getConversationList()
                    .stream().mapToInt(c->c.getMessages()
                            .stream().filter(Message::getDirection).toList()
                            .stream().mapToInt(m->m.getText().split(" ").length).sum()).sum());
            representativeStatisticsPojo.setReceivedWordCount(representative.getConversationList()
                    .stream().mapToInt(c->c.getMessages()
                            .stream().filter(m->!m.getDirection()).toList()
                            .stream().mapToInt(m->m.getText().split(" ").length).sum()).sum());
            representativeStatisticsPojo.setTotalWordCount(representative.getConversationList()
                    .stream().mapToInt(c->c.getMessages()
                            .stream().mapToInt(m->m.getText().split(" ").length).sum()).sum());
            representativeStatisticsPojo.setWorkload(representative.getWorkload());
            representativeStatisticsPojoList.add(representativeStatisticsPojo);
        }
        return representativeStatisticsPojoList;
    }
}
