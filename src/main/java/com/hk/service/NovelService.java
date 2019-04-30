package com.hk.service;

import com.hk.entity.*;
import com.hk.po.ChapterInfo;
import com.hk.po.NovelIndex;
import com.hk.po.VolumeInfo;
import com.hk.repository.*;
import com.hk.util.CommonUtil;
import com.hk.util.EntityStatus;
import com.hk.util.ResultUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.util.*;

/**
 * 提供小说操作的业务bean
 *
 * @author smallHK
 * 2019/4/27 16:36
 */
@EnableTransactionManagement
@Service
public class NovelService {


    private NovelRepository novelRepository;

    private NovelCommentRepo novelCommentRepo;

    private NovelPublishRepo novelPublishRepo;

    private VolumeRepository volumeRepository;

    private ChapterRepository chapterRepository;

    private ParagraphRepository paragraphRepository;

    public NovelService(NovelRepository novelRepository,
                        NovelCommentRepo novelCommentRepo,
                        NovelPublishRepo novelPublishRepo,
                        VolumeRepository volumeRepository,
                        ChapterRepository chapterRepository,
                        ParagraphRepository paragraphRepository) {
        this.novelRepository = novelRepository;
        this.novelCommentRepo = novelCommentRepo;
        this.novelPublishRepo = novelPublishRepo;
        this.volumeRepository = volumeRepository;
        this.chapterRepository = chapterRepository;
        this.paragraphRepository = paragraphRepository;
    }

    /**
     * 获取指定作者的指定页数的小说
     * 如果没有小说，返回空字符串
     */
    public List<Novel> findSpeicalCreatorLimitedCreatedNovelList(Integer creatorId, Integer offset, Integer length) {

        List<Novel> novelList = novelRepository.findAllByAuthorId(creatorId);
        if (Objects.isNull(novelList) || offset >= novelList.size()) {
            return new ArrayList<>();
        }

        //从offset到limit之间的数据，不包括limit
        int limit = offset + length < novelList.size() ? offset + length : novelList.size();

        return novelList.subList(offset, limit);
    }

    /**
     * 获取指定作者某发布状态的某页小说
     * 如果没有小说，返回空list
     */
    public List<Novel> findSpeCreatorLimSpePubStaNovelList(Integer creatorId, Integer pubStatus, Integer offset, Integer length) {
        List<Novel> novelList = novelRepository.findAllByStatusAndAuthorId(pubStatus, creatorId);
        if (Objects.isNull(novelList) || offset > novelList.size()) {
            return new ArrayList<>();
        }

        //从offset到limit之间的数据，不包括limit
        int limit = offset + length < novelList.size() ? offset + length : novelList.size();

        return novelList.subList(offset, limit);
    }

    /**
     * 获取指定发布状态指定页的小说列表
     */
    public List<Novel> findLimSpePubStaNovelList(Integer pubStatus, Integer offset, Integer length) {
        List<Novel> novelList = novelRepository.findAllByStatus(pubStatus);
        if (Objects.isNull(novelList) || offset > novelList.size()) {
            return new ArrayList<>();
        }

        //从offset到limit之间的数据，不包括limit
        int limit = offset + length < novelList.size() ? offset + length : novelList.size();

        return novelList.subList(offset, limit);
    }

    /**
     * 获取指定作者创作的小说数
     */
    public Integer countSpecialCreatorAllNovelList(Integer creatorId) {

        return novelRepository.countNovelByAuthorId(creatorId);
    }

    /**
     * 获取指定作者指定发布状态的小说数量
     */
    public Integer countSpeCreSpePubStaNovel(Integer creatorId, Integer status) {
        return novelRepository.countNovelByAuthorIdAndStatus(creatorId, status);
    }


    /**
     * 获取指定发布状态的小说数量
     */
    public Integer countSpePubStaNovel(Integer status) {
        return novelRepository.countAllByStatus(status);
    }


    /**
     * 修改制定小说的发布状态
     */
    public void updateNovelPublishedStatus(Integer novelId, Integer publishedStatus) {
        Novel novel = novelRepository.findNovelById(novelId);
        novel.setStatus(publishedStatus);
        novelRepository.save(novel);
    }

    /**
     * 发布小说
     */
    @Transactional
    public void publishNovel(Integer novelId, Integer editorId) {
        NovelPublish novelPublish = new NovelPublish();
        novelPublish.setEditorId(editorId);
        novelPublish.setNovelId(novelId);
        novelPublishRepo.save(novelPublish);
        Novel novel = novelRepository.findNovelById(novelId);
        novel.setStatus(EntityStatus.NOVEL_PASSED);
        novelRepository.save(novel);
    }


    /**
     * 获取编辑管理的小说列表
     */
    public List<Novel> listAllNovelByEditorId(Integer editorId) {
        List<NovelPublish> novelPublishList = novelPublishRepo.findAllByEditorId(editorId);

        List<Integer> noveIdList = new ArrayList<>();
        for(NovelPublish np: novelPublishList) {
            noveIdList.add(np.getNovelId());
        }

        Iterable<Novel> novels = novelRepository.findAllById(noveIdList);

        List<Novel> novelList = new ArrayList<>();
        for(Novel novel: novels) {
            novelList.add(novel);
        }
        return novelList;
    }


    /**
     * 返回小说的目录信息
     */
    public NovelIndex findNovelIndex(Integer novelId) {

        List<Volume> volumeList = volumeRepository.findAllByNovelId(novelId);
        List<Chapter> chapterList = chapterRepository.findAllByNovelId(novelId);
        Novel novel = novelRepository.findNovelById(novelId);


        List<VolumeInfo> volumeInfoList = new ArrayList<>();

        for (Volume volume : volumeList) {

            VolumeInfo volumeInfo = new VolumeInfo();
            volumeInfo.setVolumeTitle(volume.getVolumeTitle());
            volumeInfo.setOrderNum(volume.getOrderNum());
            volumeInfo.setVolumeId(volume.getId());

            List<Chapter> voChapterList = new ArrayList<>();
            for (Chapter chapter : chapterList) {
                if (chapter.getVolumeId().equals(volume.getId())) {
                    voChapterList.add(chapter);
                }
            }
            volumeInfo.setChapterList(voChapterList);
            volumeInfoList.add(volumeInfo);
        }
        NovelIndex index = new NovelIndex();
        index.setNovelId(novelId);
        index.setNovelName(novel.getNovelName());
        index.setVolumeInfoList(volumeInfoList);

        return index;

    }

    /**
     * 获取指定章节的内容
     */
    public ChapterInfo findNovelChapter(Integer chapterId) {

        ChapterInfo result = new ChapterInfo();

        List<Paragraph> paragraphList = paragraphRepository.findAllByChapterId(chapterId);
        paragraphList.sort(Comparator.comparing(Paragraph::getOrderNum));
        result.setParagraphList(paragraphList);

        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();
        result.setId(chapterId);
        result.setTitle(chapter.getTitle());
        result.setClickCount(chapter.getClickCount());
        result.setWordCount(chapter.getWordCount());

        Novel novel = novelRepository.findNovelById(chapter.getNovelId());
        result.setNovelId(novel.getId());
        result.setNovelTitle(novel.getNovelName());

        Volume volume = volumeRepository.findById(chapter.getVolumeId()).orElseThrow();
        result.setVolumeId(volume.getId());
        result.setVolumeTitle(volume.getVolumeTitle());


        Integer maxOrder = chapterRepository.countAllByNovelId(novel.getId());

        if(chapter.getOrderNum() <= 1) {
            result.setIsPreview(false);
        } else {
            result.setIsPreview(true);
            Chapter preChap = chapterRepository.findNovelByOrderNum(chapter.getOrderNum() - 1);
            result.setPreviewId(preChap.getId());
        }

        if(chapter.getOrderNum() >= maxOrder) {
            result.setIsNext(false);
        } else {
            result.setIsNext(true);
            Chapter nextChap = chapterRepository.findNovelByOrderNum(chapter.getOrderNum() + 1);
            result.setNovelId(nextChap.getNovelId());
        }

        return result;

    }

    /**
     * 添加新卷
     */
    public void addNewVolume(Integer novelId, String volumeTitle) {
        Volume volume = new Volume();
        volume.setNovelId(novelId);
        volume.setVolumeTitle(volumeTitle);
        Integer orderNum = volumeRepository.countAllByNovelId(novelId) + 1;
        volume.setOrderNum(orderNum);
        volumeRepository.save(volume);
    }

    /**
     * 添加新的章节
     */
    @Transactional
    public void addNewChapter(Integer novelId, Integer volumeId, String chapterTitle, String originText) {

        //文本处理
        String chapterContent = CommonUtil.textProcessing(originText);

        Integer wordCount = chapterContent.length();

        Chapter chapter = new Chapter();
        chapter.setNovelId(novelId);
        chapter.setVolumeId(volumeId);
        chapter.setTitle(chapterTitle);
        chapter.setWordCount(wordCount);

        Integer count = chapterRepository.countAllByNovelIdAndVolumeId(novelId, volumeId);
        chapter.setOrderNum(count + 1);

        Chapter resultChapter = chapterRepository.save(chapter);

        List<Paragraph> paragraphList = new ArrayList<>();
        String[] contents = chapterContent.split("\n");
        for (int i = 0; i < contents.length; i++) {
            String line = contents[i];
            Paragraph paragraph = new Paragraph();
            paragraph.setChapterId(resultChapter.getId());
            paragraph.setContent(line);
            paragraph.setOrderNum(i + 1);
            paragraph.setNovelId(novelId);
            paragraphList.add(paragraph);
        }
        paragraphRepository.saveAll(paragraphList);

    }

    /**
     * 删除执行小说的一切相关数据
     *
     * @param novelId 制定小说id
     */
    @Transactional
    public void deleteNovel(Integer novelId) {

        //删除小说
        novelRepository.deleteAllById(novelId);

        //删除小说评论
        novelCommentRepo.deleteAllByNovelId(novelId);

        //删除小说发布映射
        novelPublishRepo.deleteAllByNovelId(novelId);

        //删除小说卷
        volumeRepository.deleteAllByNovelId(novelId);

        //删除小说章节
        chapterRepository.deleteAllByNovelId(novelId);

        //删除小说段落
        paragraphRepository.deleteAllByNovelId(novelId);

    }

}

