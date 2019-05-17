package com.hk.service;

import com.hk.constant.EntityStatus;
import com.hk.constant.EventStatus;
import com.hk.entity.Editor;
import com.hk.entity.EditorRecommend;
import com.hk.entity.Profile;
import com.hk.po.NovelInfo;
import com.hk.repository.EditorRecommendRepo;
import com.hk.repository.EditorRepository;
import com.hk.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * @author smallHK
 * 2019/5/11 23:48
 */
@Service
public class EditorService {

    private EditorRepository editorRepository;

    private ProfileRepository profileRepository;

    private EditorRecommendRepo editorRecommendRepo;

    private NovelService novelService;

    public EditorService(EditorRepository editorRepository,
                         ProfileRepository profileRepository,
                         EditorRecommendRepo editorRecommendRepo,
                         NovelService novelService) {
        this.editorRepository = editorRepository;
        this.profileRepository = profileRepository;
        this.editorRecommendRepo = editorRecommendRepo;
        this.novelService = novelService;
    }

    public Profile findProfileByEditorId(Integer editorId) {
        Editor editor = editorRepository.findById(editorId).orElseThrow();
        return profileRepository.findById(editor.getProfileId()).orElseThrow();
    }


    //推荐小说
    public void recommendNovel(Integer novelId, Integer editorId, String reason) {
        EditorRecommend editorRecommend = new EditorRecommend();
        editorRecommend.setStatus(EventStatus.EDITOR_RECOMMEND_SUBMITTED);
        editorRecommend.setRecommendTime(Timestamp.from(Instant.now()));
        editorRecommend.setNovelId(novelId);
        editorRecommend.setEditorId(editorId);
        editorRecommend.setReason(reason);
        editorRecommendRepo.save(editorRecommend);
    }

    /**
     * 判断小说是否处于推荐状态
     * 小说处于推荐状态时，返回true
     */
    public boolean judgeNovelRecommendStatus(Integer novelId) {
        List<NovelInfo> infos = novelService.findCurrentEditorRecommendNovels();
        for (NovelInfo info : infos) {
            if (info.getId().equals(novelId)) return true;
        }
        return false;
    }

     /* 判断小说是否可以推荐
     * 可以推荐返回true
     * 可以推荐：不是正在推荐不是正在审核
     */
      public boolean judgeRecommendCapacity(Integer novelId) {

          //获取小说的所有推荐
          List<EditorRecommend> recommends = editorRecommendRepo.findAllByNovelId(novelId);

          //正在推荐状态，不可以推荐
          for(EditorRecommend recommend: recommends) {
              if(recommend.getStatus().equals(EventStatus.EDITOR_RECOMMEND_SUBMITTED))
                  return false;
          }

          //正在推荐中，不可以推荐
          return !judgeNovelRecommendStatus((novelId));

      }


}