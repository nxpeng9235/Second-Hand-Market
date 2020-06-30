package cn.scut.wg.secondhandmarket.domain;

public class Trade {

    private int tid;
    private String sellusername;
    private String buyusername;
    private String name;
    private String description;
    private int price;
    private String figpath;
    private int status;

    public Trade(int tid, String sellusername, String buyusername, String name, String description, int price, String figpath, int status) {
        this.tid = tid;
        this.sellusername = sellusername;
        this.buyusername = buyusername;
        this.name = name;
        this.description = description;
        this.price = price;
        this.figpath = figpath;
        this.status = status;
    }

    public int getTid() { return tid; }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getSellusername() {
        return sellusername;
    }

    public void setSellusername(String sellusername) {
        this.sellusername = sellusername;
    }

    public String getBuyusername() {
        if (buyusername.equals("##"))
            return "";
        return buyusername;
    }

    public void setBuyusername(String buyusername) {
        this.buyusername = buyusername;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getFigpath() {
        return figpath;
    }

    public void setFigpath(String figpath) {
        this.figpath = figpath;
    }

    public String[] getFigpathList() {
        String[] list = figpath.split(",");
        return list;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusString() {
        switch (status){
            case 0:
                return "在售";
            case 1:
                return "已售";
            case 2:
                return "待仲裁";
            case 3: {
                if (buyusername.equals("##")) // 未售出，已下架
                    return "已下架";
                else
                    return "已退款";
            }
            default:
                return "异常";
        }
    }

    public String getLevel() {
        if (status <= 1)
            return "primary";
        if (status == 2)
            return "warning";
        else
            return "danger";
    }

    // 此处用javascript实现失败
    public String getEditable() {
        return (status == 0) ? "false": "true";
    }

    public String getJudge() {
        return (status == 2) ? "false": "true";
    }
}
