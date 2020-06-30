package cn.scut.wg.secondhandmarket.util;

import cn.scut.wg.secondhandmarket.contract.AccountContract;
import cn.scut.wg.secondhandmarket.contract.TradeContract;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContractConfig {

    @Autowired Web3j web3j;

    @Autowired Credentials credentials;

    @Bean
    public AccountContract getAccountContract(Web3j web3j, Credentials credentials) {
        AccountContract accountContract = AccountContract.load(Constant.accountContractAddress,
                web3j, credentials, new StaticGasProvider(Constant.gasPrice, Constant.gasLimit));
        return accountContract;
    }

    @Bean
    public TradeContract getTradeContract(Web3j web3j, Credentials credentials){
        TradeContract tradeContract = TradeContract.load(Constant.tradeContractAddress,
                web3j, credentials, new StaticGasProvider(Constant.gasPrice, Constant.gasLimit));
        return tradeContract;
    }
}
