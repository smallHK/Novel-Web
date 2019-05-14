package com.hk.service;

import com.hk.constant.AdminOpeType;
import com.hk.constant.EventStatus;
import com.hk.entity.AdminOperationLog;
import com.hk.entity.EditorRecommend;
import com.hk.entity.Profile;
import com.hk.repository.AdminOperationLogRepo;
import com.hk.repository.EditorRecommendRepo;
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

    private EditorRecommendRepo editorRecommendRepo;

    private NovelService novelService;

    public AdminService(ProfileRepository profileRepository,
                        RecommendService recommendService,
                        AdminOperationLogRepo adminOperationLogRepo,
                        EditorRecommendRepo editorRecommendRepo,
                        NovelService novelService) {
        this.profileRepository = profileRepository;
        this.recommendService = recommendService;
        this.adminOperationLogRepo = adminOperationLogRepo;
        this.editorRecommendRepo = editorRecommendRepo;
        this.novelService = novelService;
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

    /**
     * 获取搜索未处理编辑推荐
     */
    public List<EditorRecommend> gainAllSubmittedEditorRecommend() {
        return editorRecommendRepo.findAllByStatus(EventStatus.EDITOR_RECOMMEND_SUBMITTED);
    }

    /**
     * 同意编辑推荐
     * 将所有对应的推荐全部同意
     */
    public void agreeEditorRecommend (Integer novelId) {
        List<EditorRecommend> recommends = editorRecommendRepo.findAllByNovelId(novelId);
        recommends.forEach(e->e.setStatus(EventStatus.EDITOR_RECOMMEND_PASSED));
        editorRecommendRepo.saveAll(recommends);

    }

}

