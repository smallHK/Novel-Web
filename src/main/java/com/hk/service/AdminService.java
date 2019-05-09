package com.hk.service;

import com.hk.entity.Profile;
import com.hk.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author smallHK
 * 2019/5/9 9:53
 */
@Service
public class AdminService {

    private ProfileRepository profileRepository;

    public AdminService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * 获取指定状态简历
     */
    public List<Profile> findAllProfileByStatus(Integer status) {
        return profileRepository.findAllByStatus(status);
    }

}

