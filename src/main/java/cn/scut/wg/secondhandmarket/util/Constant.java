package cn.scut.wg.secondhandmarket.util;

import java.math.BigInteger;

public interface Constant {

    public static final Integer CONNECT_SECONDS = 30;
    public static final Integer CONNECT_SLEEP_PER_MILLIS = 1;
    public static final Integer TIME_OUT = 30000;

    BigInteger gasPrice = new BigInteger("300000000");
    BigInteger gasLimit = new BigInteger("300000000");

    String accountContractAddress = "0x2872c9adef3718b7b4bb918ec0f3e3f535f51fd7";
    String tradeContractAddress = "0x1bb38602215b606d471db3f5d577382769635d7d";

    String projectPath = "/Volumes/Data/IdeaProjects/Second-Hand-Market/src/main/resources/static/";
    String projectBuildPath = "/Volumes/Data/IdeaProjects/Second-Hand-Market/target/classes/static/";

}
