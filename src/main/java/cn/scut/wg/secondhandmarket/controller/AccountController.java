package cn.scut.wg.secondhandmarket.controller;

import cn.scut.wg.secondhandmarket.domain.Account;
import cn.scut.wg.secondhandmarket.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;

@Controller
public class AccountController {

    @Autowired
    private volatile AccountService accountService;

    @Autowired
    private volatile HttpSession httpSession;

    @RequestMapping("/")
    public String home(){
        return "redirect:login";
    }

    @GetMapping("/login")
    public String loginGet(){
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(Account account, Model model) throws Exception {
        Account account1 = accountService.login(account);
        synchronized (this){
            if (account1 != null) {
                httpSession.setAttribute("account", account1);
                return "redirect:market";
            } else {
                model.addAttribute("error", "用户名或密码错误，请重新登录！");
                return "login";
            }
        }
    }

    @GetMapping("/register")
    public String registerGet(Model model){
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(Account account, Model model) throws Exception{
        int result = accountService.register(account);
        synchronized (this){
            if (result >= 0){
                model.addAttribute("note", "注册成功，跳转到登陆界面！");
                return "login";
            }
            else{
                model.addAttribute("error", "注册失败，请重试！");
                System.out.println("errorcode : " + result);
                return "register";
            }
        }
    }

    @GetMapping("/userManage")
    public String userManageGet(Model model) throws Exception {
        Account account = (Account) httpSession.getAttribute("account");
        Account account1 = accountService.login(account);
        model.addAttribute("account", account1);
        return "userManage";
    }

    @PostMapping("/userManage")
    public String userManagePost(Model model, Account account, HttpSession httpSession) throws Exception {
        int i = accountService.update(account);
        synchronized (this) {
            if (i != 0){
                model.addAttribute("error", "信息修改失败，请重试！");
                return "userManage";
            }
        }
        Account account1 = accountService.login(account);
        httpSession.setAttribute("account", account1);
        return "redirect:userManage";
    }

    @GetMapping("/deposit")
    public String depositGet(Model model) throws Exception {
        Account account = (Account) httpSession.getAttribute("account");
        Account account1 = accountService.login(account);
        model.addAttribute("account", account1);
        return "deposit";
    }

    @PostMapping("/deposit")
    public String depositPost(BigInteger amount, Model model) throws Exception {
        Account account = (Account) httpSession.getAttribute("account");
        int i = accountService.deposit(account, amount);
        synchronized (this) {
            if (i != 0){
                model.addAttribute("error", "充值失败，请重试！");
                return "deposit";
            }
        }
        Account account1 = accountService.login(account);
        httpSession.setAttribute("account", account1);
        return "redirect:deposit";
    }

    @GetMapping("/rootLogin")
    public String rootLoginGet() throws Exception {
        return "rootLogin";
    }

    @PostMapping("/rootLogin")
    public String rootLoginPost(Account account, Model model) throws Exception {
        synchronized (this) {
            if (account.getUsername().equals("root") && account.getPassword().equals("root")){
                httpSession.setAttribute("account", account);
                return "redirect:rootAllTrade";
            }
            else {
                model.addAttribute("error", "用户名或密码错误，请重新登录！");
                return "rootLogin";
            }
        }
    }

}