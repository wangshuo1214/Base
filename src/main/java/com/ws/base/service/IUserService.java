package com.ws.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ws.base.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService extends IService<User> {

    boolean addUser(User user);

    List<User> queryUser(User user);

    boolean deleteUser(List<String> ids);

    boolean updateUser(User user);

    User getUser(String id);

    boolean resetUserPassword(String id);

    boolean changeUserStatus(String id, String status);

    List<User> queryAllocatedUserList(User user);

    List<User> queryUnAllocatedUserList(User user);

    User getUserProfile(User user);

    boolean updateUserProfile(User user);

    boolean updateUserPwd(User user);

    String uploadAvatar(MultipartFile file, User user);
}
