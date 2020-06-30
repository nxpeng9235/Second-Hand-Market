package cn.scut.wg.secondhandmarket.util;

import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "channel-service")
public class ChannelServiceConfig {

    private String agencyName;
    private int groupID;
    private static final Logger log = LoggerFactory.getLogger(ChannelServiceConfig.class);

    @Bean
    public Service getService(GroupChannelConnectionsConfig groupChannelConnectionsConfig){
        Service channelService = new Service();
        channelService.setConnectSeconds(Constant.CONNECT_SECONDS);
        channelService.setOrgID(agencyName);
        log.info("agencyName : {}", agencyName);
        channelService.setConnectSleepPerMillis(Constant.CONNECT_SLEEP_PER_MILLIS);
        channelService.setGroupId(groupID);
        channelService.setAllChannelConnections(groupChannelConnectionsConfig);
        return channelService;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
}
