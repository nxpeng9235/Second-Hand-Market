package cn.scut.wg.secondhandmarket.controller;

import cn.scut.wg.secondhandmarket.domain.Account;
import cn.scut.wg.secondhandmarket.domain.Trade;
import cn.scut.wg.secondhandmarket.service.AccountService;
import cn.scut.wg.secondhandmarket.service.TradeService;
import cn.scut.wg.secondhandmarket.util.Constant;
import cn.scut.wg.secondhandmarket.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class MarketController {

    private volatile List<Trade> tradeList;

    @Autowired
    private volatile HttpSession httpSession;

    @Autowired
    private volatile TradeService tradeService;

    @Autowired
    private volatile AccountService accountService;

    @RequestMapping("/market")
    public String market(Model model) throws Exception {
        tradeList = tradeService.getAllSellingTrade();
        model.addAttribute("tradeList", tradeList);
        return "market";
    }

    @GetMapping("/tradeDetail")
    public String tradeDetailGet(int tid, Model model) throws Exception {
        Trade trade = tradeService.getTrade(tid);
        synchronized (this) {
            if (trade != null)
                model.addAttribute("trade", trade);
        }
        return "tradeDetail";
    }

    @PostMapping("/tradeDetail")
    @ResponseBody
    public String tradeDetailPost(int tid, Model model) throws Exception{
        Trade trade = tradeService.getTrade(tid);
        model.addAttribute("trade", trade);
        Account account = (Account) httpSession.getAttribute("account");
        String ret_message = tradeService.buyTrade(tid, account.getUsername());
        int ret_code = accountService.transfer(account.getUsername(), trade.getSellusername(), trade.getPrice());
        return ret_message;
    }

    @GetMapping("/myBuyTrade")
    public String myBuyTradeGet(Model model) throws Exception{
        Account account = (Account) httpSession.getAttribute("account");
        List<Trade> myBuyTradeList = tradeService.getMyBuyTrade(account.getUsername());
        model.addAttribute("myBuyTradeList", myBuyTradeList);
        return "myBuyTrade";
    }

    @PostMapping("/myBuyTrade")
    public String myBuyTradePost(int tid, Model model) throws Exception{
        // 申请退款
        int ret_code = tradeService.applyJudge(tid);
        synchronized (this) {
            if (ret_code < 0){
                model.addAttribute("error", "申请失败！");
                return "myBuyTrade";
            }
        }
        return "redirect:myJudgeTrade";
    }

    @GetMapping("/mySellTrade")
    public String mySellTradeGet(Model model) throws Exception{
        Account account = (Account) httpSession.getAttribute("account");
        List<Trade> mySellTradeList = tradeService.getMySellTrade(account.getUsername());
        model.addAttribute("mySellTradeList", mySellTradeList);
        return "mySellTrade";
    }

    @PostMapping("/mySellTrade")
    public String mySellTradePost(int tid, Model model) throws Exception{
        // 删除交易
        int ret_code = tradeService.deleteTrade(tid);
        synchronized (this) {
            if (ret_code < 0){
                model.addAttribute("error", "删除失败！");
                return "mySellTrade";
            }
        }
        return "redirect:mySellTrade";
    }

    @RequestMapping("/myJudgeTrade")
    public String myJudgeTrade(Model model) throws Exception{
        Account account = (Account) httpSession.getAttribute("account");
        List<Trade> myJudgeTradeList = tradeService.getMyJudgeTrade(account.getUsername());
        model.addAttribute("myJudgeTradeList", myJudgeTradeList);
        return "myJudgeTrade";
    }

    @GetMapping("/newTrade")
    public String newTradeGet() throws Exception {
        return "newTrade";
    }

    @PostMapping("/newTrade")
    @ResponseBody
    public synchronized String newTradePost(String name, int price, String description, @RequestParam(value = "imgList") List<MultipartFile> imgList, Model model) throws Exception {
        StringBuffer imgPaths = new StringBuffer("");
        for (MultipartFile file : imgList){
            // 获取图片原始文件名
            String originalFilename = file.getOriginalFilename();
            // 文件名使用当前时间
            String time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            // 获取上传图片的扩展名(jpg/png/...)
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            // 图片上传的相对路径（因为相对路径放到页面上就可以显示图片）
            String path = "upload/" + time + extension;
            // 图片上传的绝对路径
//            String url = request.getSession().getServletContext().getRealPath("") + path;
            String url1 = "/Volumes/Data/IdeaProjects/Second-Hand-Market/src/main/resources/static/" + path;
            String url2 = "/Volumes/Data/IdeaProjects/Second-Hand-Market/target/classes/static/" + path;
            File dir = new File(url1);
            if (!dir.exists()) dir.mkdirs();
            File dir1 = new File(url2);
            if (!dir1.exists()) dir1.mkdirs();
            // 将图片上传到本地
            file.transferTo(new File(url1));
            file.transferTo(new File(url2));
            imgPaths.append(path);
            imgPaths.append(",");
            ImageUtil.resize(url1, url1, 400, 540);
            ImageUtil.resize(url2, url2, 400, 540);
        }
        String imgPathsString = imgPaths.toString();
        Account account = (Account) httpSession.getAttribute("account");
        String ret_message = tradeService.newTrade(account.getUsername(), name, description, price, imgPathsString);
        return ret_message;
    }

    @GetMapping("editTrade")
    public String editTradeGet(int tid, Model model) throws Exception {
        Trade trade = tradeService.getTrade(tid);
        if (trade != null)
            model.addAttribute("trade", trade);
        return "editTrade";
    }

    @PostMapping("editTrade")
    @ResponseBody
    public synchronized String editTradePost(int tid, String name, int price, String description, @RequestParam(value = "imgList") List<MultipartFile> imgList, Model model) throws Exception {
        Trade trade = tradeService.getTrade(tid);
        String newFigPath = trade.getFigpath();
        // 如果未更新图片，则不做修改
        if (!(imgList == null || imgList.size() == 0)){
            StringBuffer imgPaths = new StringBuffer("");
            for (MultipartFile file : imgList){
                // 获取图片原始文件名
                String originalFilename = file.getOriginalFilename();
                // 文件名使用当前时间
                String time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                // 获取上传图片的扩展名(jpg/png/...)
                String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                // 图片上传的相对路径（因为相对路径放到页面上就可以显示图片）
                String path = "upload/" + time + extension;
                // 图片上传的绝对路径
//            String url = request.getSession().getServletContext().getRealPath("") + path;
                String url1 = Constant.projectPath + path;
                String url2 = Constant.projectBuildPath + path;
                File dir1 = new File(url1);
                if (!dir1.exists()) dir1.mkdirs();
                File dir2 = new File(url2);
                if (!dir2.exists()) dir2.mkdirs();
                // 将图片上传到本地
                file.transferTo(new File(url1));
                file.transferTo(new File(url2));
                imgPaths.append(path);
                imgPaths.append(",");
                ImageUtil.resize(url1, url1, 400, 540);
                ImageUtil.resize(url2, url2, 400, 540);
            }
            newFigPath = imgPaths.toString();
            // 如果更新了照片，将之前的图片删除
            for (String imgPath : trade.getFigpathList()) {
                String imgFullPath1 = Constant.projectPath + imgPath;
                File file1 = new File(imgFullPath1);
                file1.delete();
                String imgFullPath2 = Constant.projectBuildPath + imgPath;
                File file2 = new File(imgFullPath2);
                file2.delete();
            }
        }
        String ret_message = tradeService.updateTrade(trade.getTid(), trade.getSellusername(), trade.getBuyusername(), name,
                description, price, newFigPath, trade.getStatus());
//        return "redirect:tradeDetail?tid="+tid;
        return ret_message;
    }

    @RequestMapping("/rootAllTrade")
    public String rootAllTrade(Model model) throws Exception {
        if (!((Account)httpSession.getAttribute("account")).getUsername().equals("root"))
            return "redirect:login";
        List<Trade> allTradeList = tradeService.getAllTrade();
        model.addAttribute("allTradeList", allTradeList);
        return "rootAllTrade";
    }

    @RequestMapping("/rootTradeDetail")
    public String rootTradeDetail(int tid, Model model) throws Exception {
        if (!((Account)httpSession.getAttribute("account")).getUsername().equals("root"))
            return "redirect:login";
        Trade trade = tradeService.getTrade(tid);
        if (trade != null)
            model.addAttribute("trade", trade);
        return "rootTradeDetail";
    }

    @GetMapping("/rootAllJudgeTrade")
    public String rootAllJudgeTradeGet(Model model) throws Exception {
        if (!((Account)httpSession.getAttribute("account")).getUsername().equals("root"))
            return "redirect:login";
        List<Trade> allJudgeTradeList = tradeService.getAllJudgeTrade();
        model.addAttribute("allJudgeTradeList", allJudgeTradeList);
        return "rootAllJudgeTrade";
    }

    @PostMapping("/rootAllJudgeTrade")
    public String rooAllJudgeTradePost(int tid, boolean pass, Model model) throws Exception {
        synchronized (this) {
            int ret_code = tradeService.judge(tid, pass);
            if (pass){
                Trade trade = tradeService.getTrade(tid);
                ret_code += accountService.transfer(trade.getSellusername(), trade.getBuyusername(), trade.getPrice());
            }
            if (ret_code < 0){
                model.addAttribute("error", "操作失败！请重试！");
                return "rootAllJudgeTrade";
            }
        }
        return "redirect:rootAllJudgeTrade";
    }
}
