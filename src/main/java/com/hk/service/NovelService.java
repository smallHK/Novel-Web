package com.hk.service;

import com.hk.entity.*;
import com.hk.po.*;
import com.hk.repository.*;
import com.hk.util.CommonUtil;
import com.hk.util.EntityStatus;
import com.hk.util.EventType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

    private CreatorRepository creatorRepository;

    private ReaderRepo readerRepo;

    private ChapterPublishEventRepo chapterPublishEventRepo;

    private VolumePublishEventRepo volumePublishEventRepo;

    public NovelService(NovelRepository novelRepository,
                        NovelCommentRepo novelCommentRepo,
                        NovelPublishRepo novelPublishRepo,
                        VolumeRepository volumeRepository,
                        ChapterRepository chapterRepository,
                        ParagraphRepository paragraphRepository,
                        CreatorRepository creatorRepository,
                        ReaderRepo readerRepo,
                        ChapterPublishEventRepo chapterPublishEventRepo,
                        VolumePublishEventRepo volumePublishEventRepo) {
        this.novelRepository = novelRepository;
        this.novelCommentRepo = novelCommentRepo;
        this.novelPublishRepo = novelPublishRepo;
        this.volumeRepository = volumeRepository;
        this.chapterRepository = chapterRepository;
        this.paragraphRepository = paragraphRepository;
        this.creatorRepository = creatorRepository;
        this.readerRepo = readerRepo;
        this.chapterPublishEventRepo = chapterPublishEventRepo;
        this.volumePublishEventRepo = volumePublishEventRepo;
    }

    /**
     * 获取小说列表
     * 条件：页、作者
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
     * 获取小说列表
     * 条件：页、发布状态、作者Id
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
     * 获取小说列表
     * 条件：发布状态、页
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
     * 获取小说列表
     * 条件：小说名关键字
     * 默认条件：搜索开放小说
     * 关键字为null时，搜索全部小说
     * 目前只能完整匹配
     */
    public List<Novel> findNovelListByKeyword(String keyword) {

        List<Novel> novelList;
        if (Objects.isNull(keyword) || keyword.equals("")) {
            novelList = novelRepository.findAllByStatus(EntityStatus.NOVEL_PASSED);
        } else {
            //novelList = novelRepository.findAllByNovelNameAndStatus(keyword, EntityStatus.NOVEL_PASSED);
            novelList = novelRepository.findAllByNovelNameContainingAndStatus(keyword, EntityStatus.NOVEL_PASSED);
        }
        if (Objects.isNull(novelList)) {
            return new ArrayList<>();
        }
        return novelList;
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
        for (NovelPublish np : novelPublishList) {
            noveIdList.add(np.getNovelId());
        }

        Iterable<Novel> novels = novelRepository.findAllById(noveIdList);

        List<Novel> novelList = new ArrayList<>();
        for (Novel novel : novels) {
            novelList.add(novel);
        }
        return novelList;
    }

    /**
     * 获取小说信息
     */
    public NovelInfo findNovelInfo(Integer novelId) {
        Novel novel = novelRepository.findNovelById(novelId);

        Integer authorId = novel.getAuthorId();
        Creator creator = creatorRepository.findById(authorId).orElseThrow();

        NovelInfo novelInfo = new NovelInfo();
        novelInfo.setAuthorId(authorId);
        novelInfo.setBriefIntro(novel.getBriefIntro());
        novelInfo.setPenName(creator.getPenName());
        novelInfo.setId(novelId);
        novelInfo.setNovelName(novel.getNovelName());
        novelInfo.setCoverImg(novel.getCoverImg());

        return novelInfo;
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
        result.setStatus(chapter.getStatus());

        Novel novel = novelRepository.findNovelById(chapter.getNovelId());
        result.setNovelId(novel.getId());
        result.setNovelTitle(novel.getNovelName());

        Volume volume = volumeRepository.findById(chapter.getVolumeId()).orElseThrow();
        result.setVolumeId(volume.getId());
        result.setVolumeTitle(volume.getVolumeTitle());


        //错误代码
        //下一章的预测不是通过order与novel
        //获取最大卷
        Integer maxVolumeOrder = volumeRepository.countAllByNovelId(novel.getId());
        //获取当前卷最大章数
        Integer maxChapterOrder = chapterRepository.countAllByNovelIdAndVolumeId(novel.getId(), chapter.getVolumeId());

        //当前章为本卷第一章
        //当前卷为本小说第一卷
        //不设置上一章
        Optional.of(new Object()).filter(e->chapter.getOrderNum() == 1).filter(e->volume.getOrderNum() == 1).ifPresent(e->result.setIsPreview(false));

        //当前小说为本卷第一章
        //当前卷并不是本小说第一卷
        //将上一卷最后一章设为最终章
        Optional.of(new Object()).filter(e->chapter.getOrderNum() == 1).filter(e->volume.getOrderNum() > 1).ifPresent(e->{

            //获取上一卷
            Volume preVolume = volumeRepository.findAllByNovelIdAndOrderNum(novel.getId(), volume.getOrderNum() - 1);
            //如果上一卷为空
            Integer preVolChaNum = chapterRepository.countAllByNovelIdAndVolumeId(novel.getId(), preVolume.getId());

            if(preVolChaNum > 0) {
                //获取上一卷最后一章
                Chapter preChaper = chapterRepository.findAllByVolumeIdAndNovelIdAndOrderNum(preVolume.getId(), novel.getId(), preVolChaNum);
                result.setIsPreview(true);
                result.setPreviewId(preChaper.getId());
            }else {
                result.setIsPreview(false);
            }

        });

        //当前小说不是本卷第一章
        Optional.of(new Object()).filter(e->chapter.getOrderNum() > 1).ifPresent(e->{
            result.setIsPreview(true);
            Chapter preChap = chapterRepository.findAllByVolumeIdAndNovelIdAndOrderNum(chapter.getVolumeId(), novel.getId(), chapter.getOrderNum() - 1);
            result.setPreviewId(preChap.getId());
        });

        //当前章为本卷最后一章
        //当前卷为本小说最后一卷
        //不设置下一章
        Optional.of(new Object()).filter(e->chapter.getOrderNum().equals(maxChapterOrder)).filter(e->volume.getOrderNum().equals(maxVolumeOrder)).ifPresent(e->result.setIsNext(false));

        //当前小说为本卷最后一章
        //当前卷并不是本小说最后一卷
        //将下一卷第一章设置为下一章
        Optional.of(new Object()).filter(e->chapter.getOrderNum().equals(maxChapterOrder)).filter(e->volume.getOrderNum() < maxVolumeOrder).ifPresent(e->{

            //获取下一卷第一章
            Volume nextVolume = volumeRepository.findAllByNovelIdAndOrderNum(novel.getId(), volume.getOrderNum() + 1);

            //如果下一卷为空
            Integer nextVolChaNum = chapterRepository.countAllByNovelIdAndVolumeId(novel.getId(), nextVolume.getId());

            if(nextVolChaNum > 0) {
                //获取下一卷第一章
                Chapter nextChaper = chapterRepository.findAllByVolumeIdAndNovelIdAndOrderNum(nextVolume.getId(), novel.getId(), 1);
                result.setIsNext(true);
                result.setNextId(nextChaper.getId());
            }else {
                result.setIsNext(false);
            }
        });

        //当前小说不是本卷最后一章
        Optional.of(new Object()).filter(e->chapter.getOrderNum() < maxChapterOrder).ifPresent(e->{
            result.setIsNext(true);
            Chapter preChap = chapterRepository.findAllByVolumeIdAndNovelIdAndOrderNum(chapter.getVolumeId(), novel.getId(), chapter.getOrderNum() + 1);
            result.setNextId(preChap.getId());
        });


        return result;

    }

    /**
     * 添加新小说
     */
    public void addNewNovel(Integer creatorId, String novelName, String briefIntro, String coverFileName, byte[] coverData) {

        try {
            Path parent = Path.of("D:\\CurriculumDesign\\Novel-Web\\src\\main\\resources\\static\\data\\novelCover\\cover", creatorId.toString());
            Files.createDirectories(parent);
            Path target = Path.of(parent.toString(), coverFileName);

            if (!Files.exists(target)) {
                Files.createFile(target);
                Files.write(target, coverData);
            }
            Novel novel = new Novel();
            novel.setAuthorId(creatorId);
            novel.setBriefIntro(briefIntro);
            novel.setNovelName(novelName);
            novel.setCreateTime(Timestamp.from(Instant.now()));
            novel.setUpdateTime(Timestamp.from(Instant.now()));
            novel.setCompleteStatus(EntityStatus.NOVEL_UNCOMPLETED);
            novel.setCoverImg("/data/novelCover/cover/" + creatorId + "/" + coverFileName);
            novel.setStatus(0);
            novelRepository.save(novel);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
     * 添加评论
     */
    public void publishNovelComment(Integer readerId, Integer novelId, String originContent) {

        NovelComment novelComment = new NovelComment();
        novelComment.setContent(originContent);
        novelComment.setReaderId(readerId);
        novelComment.setNovelId(novelId);
        Integer number = novelCommentRepo.countAllByNovelId(novelId);
        novelComment.setOrderNum(number + 1);
        novelComment.setCreateTime(Timestamp.from(Instant.now()));
        novelCommentRepo.save(novelComment);

    }

    /**
     * 更新章节
     */
    public void updateChapter(Integer novelId, Integer volumeId, Integer chapterId,  String chapterTitle, String originText) {

        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();
        String chapterContent = CommonUtil.textProcessing(originText);

        //清楚所有段落

        //更新章节信息

        //发布章节更新事件



    }

    /**
     * 获取小说的评论信息列表
     * 条件：小说id
     */
    public List<NovelCommentInfo> listAllNovelCommentInfo(Integer novelId) {

        List<NovelComment> commentList = novelCommentRepo.findAllByNovelId(novelId);
        List<NovelCommentInfo> infoList = new ArrayList<>();
        for(NovelComment comment: commentList) {

            NovelCommentInfo info = new NovelCommentInfo();
            info.setId(comment.getId());
            info.setContent(Arrays.asList(comment.getContent().split("\n")));
            info.setOrderNum(comment.getOrderNum());

            Instant commentTime = comment.getCreateTime().toInstant();
            info.setCommentTime(commentTime);
            info.setCommentTimeStr(LocalDateTime.ofInstant(commentTime, ZoneOffset.ofHours(8)).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

            info.setReaderId(comment.getReaderId());
            info.setUsername(readerRepo.findById(comment.getReaderId()).orElseThrow().getUsername());
            info.setNovelId(comment.getNovelId());

            infoList.add(info);

        }
        return infoList;
    }

    /**
     * 添加指定章节点击数
     * 增加1的点击量
     */
    public void addClickNum(Integer chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();
        chapter.setClickCount(chapter.getClickCount() + 1);
        chapterRepository.save(chapter);
    }


    /**
     * 发布章节开放请求
     */
    public void publishChapterPublishEvent(Integer chapterId, Integer authorId) {

        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();

        ChapterPublishEvent event = new ChapterPublishEvent();
        event.setApplyTime(Timestamp.from(Instant.now()));
        event.setAuthorId(authorId);
        event.setChapterId(chapterId);

        Integer novelId = chapter.getNovelId();
        NovelPublish novelPublish = novelPublishRepo.findByNovelId(novelId);

        event.setEditorId(novelPublish.getEditorId());
        event.setStatus(EntityStatus.CHAPTER_PUBLISH_EVENT_SUBMITTED);
        chapterPublishEventRepo.save(event);

    }

    /**
     * 修改章节的发布状态
     */
    public void updateChapterPublishStatus(Integer chapterId, Integer status) {
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();
        chapter.setStatus(status);
        chapterRepository.save(chapter);
    }

    /**
     * 发布卷开放请求
     */
    public void publishVolumePublishEvent(Integer volumeId, Integer authorId) {
        Volume volume = volumeRepository.findById(volumeId).orElseThrow();

        VolumePublishEvent event = new VolumePublishEvent();
        event.setApplyTime(Timestamp.from(Instant.now()));
        event.setAuthorId(authorId);
        event.setVolumeId(volumeId);

        Integer novelId = volume.getNovelId();
        NovelPublish novelPublish = novelPublishRepo.findByNovelId(novelId);

        event.setEditorId(novelPublish.getEditorId());
        event.setStatus(EntityStatus.VOLUME_PUBLISH_EVENT_SUBMITTED);
        volumePublishEventRepo.save(event);

    }

    /**
     * 修改卷的发布状态
     */
    public void updateVolumePublishStatus(Integer volumeId, Integer status) {
        Volume volume = volumeRepository.findById(volumeId).orElseThrow();
        volume.setStatus(status);
        volumeRepository.save(volume);
    }


    /**
     * 获取卷发布事件
     * 条件：编辑id、指定状态的事件
     */
    public List<EditorWorkEvent> findAllVolumePublishEvent(Integer editorId, Integer status) {
        List<EditorWorkEvent> eventList = new ArrayList<>();

        List<VolumePublishEvent> publishEventList = volumePublishEventRepo.findAllByEditorIdAndStatus(editorId, status);
        for(VolumePublishEvent publishEvent: publishEventList) {
            EditorWorkEvent event = new EditorWorkEvent();
            event.setId(publishEvent.getId());
            event.setAppearTime(publishEvent.getApplyTime().toInstant());
            event.setType(EventType.VOLUME_PUBLISH_EVENT);
            event.setVolumeId(publishEvent.getVolumeId());
            event.setAuthorId(publishEvent.getAuthorId());
            event.setAppearTimeStr(LocalDateTime.ofInstant(publishEvent.getApplyTime().toInstant(), ZoneOffset.ofHours(8))
                    .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

            Volume volume = volumeRepository.findById(publishEvent.getVolumeId()).orElseThrow();
            event.setVolumeTitle(volume.getVolumeTitle());
            Novel novel = novelRepository.findById(volume.getNovelId()).orElseThrow();
            event.setNovelTitle(novel.getNovelName());
            eventList.add(event);
        }
        return eventList;
    }

    /**
     * 同意卷发布
     */
    public void agreeVolumePublish(Integer eventId, Integer status) {
        VolumePublishEvent event = volumePublishEventRepo.findById(eventId).orElseThrow();
        event.setStatus(EntityStatus.VOLUME_PUBLISH_EVENT_PASSED);
        volumePublishEventRepo.save(event);
        Volume volume = volumeRepository.findById(event.getVolumeId()).orElseThrow();
        volume.setStatus(EntityStatus.VOLUME_PASSED);
        volumeRepository.save(volume);
    }




    /**
     * 获取卷信息
     */
    public VolumeInfo findVolumeInfo(Integer volumeId) {
        List<Chapter> chapterList = chapterRepository.findAllByVolumeId(volumeId);
        Volume volume = volumeRepository.findById(volumeId).orElseThrow();
        VolumeInfo volumeInfo = new VolumeInfo();
        volumeInfo.setChapterList(chapterList);
        volumeInfo.setVolumeId(volumeId);
        volumeInfo.setVolumeTitle(volume.getVolumeTitle());
        volumeInfo.setOrderNum(volume.getOrderNum());
        volumeInfo.setStatus(volume.getStatus());
        volumeInfo.setNovelId(volume.getNovelId());
        return volumeInfo;
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

