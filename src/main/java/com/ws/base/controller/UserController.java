package com.ws.base.controller;

import com.ws.base.entity.PageQuery;
import com.ws.base.entity.Result;
import com.ws.base.model.User;
import com.ws.base.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController{

    @Autowired
    private IUserService iUserService;

    @PostMapping("/add")
    public Result addBmUser(@RequestBody User user){
        return computeResult(iUserService.addUser(user));
    }

    @PostMapping("/query")
    public Result queryUser(@RequestBody PageQuery pageQuery){
        startPage(pageQuery);
        User user = getPageItem(pageQuery,User.class);
        return success(formatTableData(iUserService.queryUser(user)));
    }

    @PostMapping("/delete")
    public Result deleteUser(@RequestBody List<String> ids){
        return computeResult(iUserService.deleteUser(ids));
    }

    @PostMapping("/update")
    public Result updateBmUser(@RequestBody User user){
        return computeResult(iUserService.updateUser(user));
    }

    @GetMapping("/get")
    public Result getUser(String id){
        return success(iUserService.getUser(id));
    }

    @PostMapping("/reset")
    public Result resetUserPassword(String id){
        return computeResult(iUserService.resetUserPassword(id));
    }

    @PostMapping("/changeStatus")
    public Result changeUserStatus(String id, String status){
        return computeResult(iUserService.changeUserStatus(id,status));
    }

    @PostMapping("/allocated")
    public Result queryAlllocatedUserList(@RequestBody PageQuery pageQuery){
        startPage(pageQuery);
        User user = getPageItem(pageQuery,User.class);
        return success(formatTableData(iUserService.queryAllocatedUserList(user)));
    }

    @PostMapping("/unAllocated")
    public Result queryUnlocatedUserList(@RequestBody PageQuery pageQuery){
        startPage(pageQuery);
        User user = getPageItem(pageQuery,User.class);
        return success(formatTableData(iUserService.queryUnAllocatedUserList(user)));
    }

    @GetMapping("/getProfile")
    public Result getUserProfile(){
        return success(iUserService.getUserProfile(getUser()));
    }

    @PostMapping("/updateProfile")
    public Result updateUserProfile(@RequestBody User user){
        return computeResult(iUserService.updateUserProfile(user));
    }

    @PostMapping("/updatePwd")
    public Result updateUserPwd(@RequestBody User user){
        user.setId(getUserId());
        return computeResult(iUserService.updateUserPwd(user));
    }

    @PostMapping("/avatar")
    public Result uploadAvatar(@RequestParam("avatarfile") MultipartFile file){
        return success(iUserService.uploadAvatar(file,getUser()));
    }
}
