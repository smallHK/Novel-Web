package com.hk.service;

import com.hk.constant.AdminOpeType;
import com.hk.entity.AdminOperationLog;
import com.hk.entity.Profile;
import com.hk.repository.AdminOperationLogRepo;
import com.hk.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * @author smallHK
 * 2019/5/9 9:53
 */
@Service
@EnableTransactionManagement
public class AdminService {

    private ProfileRepository profileRepository;

    private RecommendService recommendService;

    private AdminOperationLogRepo adminOperationLogRepo;

    public AdminService(ProfileRepository profileRepository,
                        RecommendService recommendService,
                        AdminOperationLogRepo adminOperationLogRepo) {
        this.profileRepository = profileRepository;
        this.recommendService = recommendService;
        this.adminOperationLogRepo = adminOperationLogRepo;
    }

    /**
     * 获取指定状态简历
     */
    public List<Profile> findAllProfileByStatus(Integer status) {
        return profileRepository.findAllByStatus(status);
    }

    /**
     * 计算余弦值
     */
    @Transactional
    public void calculateCosineSim() {

        AdminOperationLog adminOperationLog = new AdminOperationLog();
        adminOperationLog.setLog("测试余弦分析功能");
        adminOperationLog.setType(AdminOpeType.COSINE_SIM);
        adminOperationLog.setOperateTime(Timestamp.from(Instant.now()));
        adminOperationLogRepo.save(adminOperationLog);
        recommendService.calculateAllReaderVector();


    }

}

