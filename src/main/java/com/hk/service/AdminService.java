package com.hk.service;

import com.hk.constant.AdminOpeType;
import com.hk.constant.EventStatus;
import com.hk.constant.SessionProperty;
import com.hk.controller.CreatorRedirectController;
import com.hk.entity.*;
import com.hk.po.EditorRecommendInfo;
import com.hk.repository.*;
import com.hk.util.ResultUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private NovelRepository novelRepository;

    private CreatorRepository creatorRepository;

    private ChapterRepository chapterRepository;

    private FavoriteRepo favoriteRepo;

    private EditorRepository editorRepository;

    private AdminRepository adminRepository;

    public AdminService(ProfileRepository profileRepository,
                        RecommendService recommendService,
                        AdminOperationLogRepo adminOperationLogRepo,
                        EditorRecommendRepo editorRecommendRepo,
                        NovelRepository novelRepository,
                        CreatorRepository creatorRepository,
                        ChapterRepository chapterRepository,
                        FavoriteRepo favoriteRepo,
                        EditorRepository editorRepository,
                        AdminRepository adminRepository) {
        this.profileRepository = profileRepository;
        this.recommendService = recommendService;
        this.adminOperationLogRepo = adminOperationLogRepo;
        this.editorRecommendRepo = editorRecommendRepo;
        this.novelRepository = novelRepository;
        this.creatorRepository = creatorRepository;
        this.chapterRepository = chapterRepository;
        this.favoriteRepo = favoriteRepo;
        this.editorRepository = editorRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * 登陆功能
     * 成功登陆返回admin
     * 失败返回null
     */
    public Admin login(String username, String password) {
        Iterable<Admin> adminList = adminRepository.findAll();
        for (Admin admin :adminList) {
            if(admin.getUsername().equals(username) && admin.getPassword().equals(password)){
                return admin;
            }
        }
        return null;
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
     * 获取最后一次计算余弦值事件
     */
    public String gainLastCalConsineTime() {
        List<AdminOperationLog> ops = adminOperationLogRepo.findAllByTypeOrderByOperateTimeDesc(AdminOpeType.COSINE_SIM);
        if(Objects.nonNull(ops) && !ops.isEmpty()) {
            return LocalDateTime.ofInstant(ops.get(0).getOperateTime().toInstant(), ZoneOffset.ofHours(8))
                    .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        }

        return null;
    }

    /**
     * 获取搜索未处理编辑推荐
     */
    public List<EditorRecommendInfo> gainAllSubmittedEditorRecommend() {
        List<EditorRecommend> recommends = editorRecommendRepo.findAllByStatus(EventStatus.EDITOR_RECOMMEND_SUBMITTED);
        List<EditorRecommendInfo> infos = new ArrayList<>();
        for(EditorRecommend recommend: recommends) {
            EditorRecommendInfo info = new EditorRecommendInfo();
            info.setId(recommend.getId());
            info.setReason(recommend.getReason());
            info.setNovelId(recommend.getNovelId());


            Novel novel = novelRepository.findById(recommend.getNovelId()).orElseThrow();
            info.setNovelName(novel.getNovelName());
            info.setBriefIntro(novel.getBriefIntro());
            info.setCoverImg(novel.getCoverImg());

            Creator creator = creatorRepository.findById(novel.getAuthorId()).orElseThrow();
            info.setAuthorName(creator.getPenName());

            List<Chapter> chapters = chapterRepository.findAllByNovelId(novel.getId());
            info.setClickCount(chapters.stream().mapToInt(Chapter::getClickCount).sum());
            info.setWordCount(chapters.stream().mapToInt(Chapter::getWordCount).sum());

            info.setFavoriteCount(favoriteRepo.countAllByNovelId(novel.getId()));

            Editor editor = editorRepository.findById(recommend.getEditorId()).orElseThrow();
            info.setEditorName(editor.getEditorName());


            infos.add(info);
        }
        return infos;
    }

    /**
     * 同意编辑推荐
     * 将所有对应的推荐全部同意
     */
    public void agreeEditorRecommend (Integer novelId) {
        List<EditorRecommend> recommends = editorRecommendRepo.findAllByNovelIdAndStatus(novelId, EventStatus.EDITOR_RECOMMEND_SUBMITTED);
        recommends.forEach(e->e.setStatus(EventStatus.EDITOR_RECOMMEND_PASSED));
        editorRecommendRepo.saveAll(recommends);

    }

    /**
     * 拒绝所有推荐
     */
    public void rejectEditorRecommend(Integer novelId) {
        List<EditorRecommend> recommends = editorRecommendRepo.findAllByNovelIdAndStatus(novelId, EventStatus.EDITOR_RECOMMEND_SUBMITTED);
        recommends.forEach(e->e.setStatus(EventStatus.EDITOR_RECOMMEND_FAILURE));
        editorRecommendRepo.saveAll(recommends);
    }

}

