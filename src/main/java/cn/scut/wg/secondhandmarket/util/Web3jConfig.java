package cn.scut.wg.secondhandmarket.util;

import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.precompile.crud.CRUDService;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class Web3jConfig {

    @Bean
    public Web3j getWeb3j(Service service) throws Exception{
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        service.run();
        channelEthereumService.setChannelService(service);
        channelEthereumService.setTimeout(Constant.TIME_OUT);
        return Web3j.build(channelEthereumService, service.getGroupId());
    }

    @Bean
    public Credentials getCredentials() throws Exception{
        Credentials credentials = GenCredential.create(Keys.createEcKeyPair());
        return credentials;
    }

    @Bean
    public CRUDService getCrudService(Web3j web3j, Credentials credentials) throws Exception{
        CRUDService crudService = new CRUDService(web3j, credentials);
        return crudService;
    }
}
