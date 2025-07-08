package com.grepp.funfun.app.controller.web.admin;

import com.grepp.funfun.app.model.user.dto.UserDTO;
import com.grepp.funfun.app.model.admin.service.AdminUserService;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import com.grepp.funfun.app.model.auth.code.Role;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    private void checkAdminRole(UserDTO user) {
        if(user.getRole() != Role.ROLE_ADMIN) {
            throw new CommonException(ResponseCode.NOT_FOUND);
        }
    }

    // 유저 리스트 조회
    @GetMapping
    public String listUsers(Model model, Authentication authentication) {
        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        List<UserDTO> users = adminUserService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/user/list"; // templates / admin / user / list.html 일때
    }

    // 유저 상세 조회
    @GetMapping("/{email}")
    public String viewUser(@PathVariable String email,
                           @RequestParam (required = false) boolean suspended,
                           Model model,
                           Authentication authentication) {
        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        UserDTO user = adminUserService.getUser(email);
        model.addAttribute("user", user);
        model.addAttribute("suspended", suspended);
        return "admin/user/detail"; // tempolates / admin/ user/ detail.html 일때
    }

    // 유저 제재
    @PostMapping("/{email}/suspend")
    public String suspendUser(@PathVariable String email,
                              @RequestParam int duration,
                              @RequestParam(required = false) String reason,
                              Authentication authentication,
                              HttpServletRequest request,
                              Model model) {
        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        adminUserService.suspendUser(email, duration, reason);
        return "redirect:/admin/users/" + email + "?suspended=true";
    }

    // 유저 검색
    @GetMapping("/search")
    public String searchUserByNickname(@RequestParam String nickname,
                                       Authentication authentication){
        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        UserDTO user = adminUserService.getUserByNickname(nickname);
        return "redirect:/admin/users/" + user.getEmail();
    }
}
