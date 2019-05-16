package com.hk.service;

import com.hk.constant.EntityStatus;
import com.hk.constant.EventStatus;
import com.hk.constant.EventType;
import com.hk.entity.*;
import com.hk.entity.event.ChapterPublishEvent;
import com.hk.entity.event.VolumePublishEvent;
import com.hk.po.*;
import com.hk.repository.*;
import com.hk.repository.event.ChapterPublishEventRepo;
import com.hk.repository.event.VolumePublishEventRepo;
import com.hk.util.CollectionUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    private FavoriteRepo favoriteRepo;

    private TagRepo tagRepo;

    private TagNovelRelationRepo tagNovelRelationRepo;

    private RecommendPriorityRepo recommendPriorityRepo;

    private EditorRecommendRepo editorRecommendRepo;

    private NovelInfoService novelInfoService;

    public NovelService(NovelRepository novelRepository,
                        NovelCommentRepo novelCommentRepo,
                        NovelPublishRepo novelPublishRepo,
                        VolumeRepository volumeRepository,
                        ChapterRepository chapterRepository,
                        ParagraphRepository paragraphRepository,
                        CreatorRepository creatorRepository,
                        ReaderRepo readerRepo,
                        ChapterPublishEventRepo chapterPublishEventRepo,
                        VolumePublishEventRepo volumePublishEventRepo,
                        FavoriteRepo favoriteRepo,
                        TagRepo tagRepo,
                        TagNovelRelationRepo tagNovelRelationRepo,
                        RecommendPriorityRepo recommendPriorityRepo,
                        EditorRecommendRepo editorRecommendRepo,
                        NovelInfoService novelInfoService) {
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
        this.favoriteRepo = favoriteRepo;
        this.tagRepo = tagRepo;
        this.tagNovelRelationRepo = tagNovelRelationRepo;
        this.recommendPriorityRepo = recommendPriorityRepo;
        this.editorRecommendRepo = editorRecommendRepo;
        this.novelInfoService = novelInfoService;
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
     * 获取所有小说
     */
    public List<NovelInfo> findAllNovelInfo() {
        List<Novel> novels = CollectionUtil.iterableToList(novelRepository.findAll());
        List<NovelInfo> infos = new ArrayList<>();
        for(Novel novel: novels) {
            infos.add(novelToNovelInfo(novel));
        }
        return infos;
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
        novelPublish.setPublishTime(Timestamp.from(Instant.now()));
        novelPublishRepo.save(novelPublish);
        Novel novel = novelRepository.findNovelById(novelId);
        novel.setStatus(EntityStatus.NOVEL_PASSED);
        novelRepository.save(novel);
    }


    /**
     * 获取编辑管理的小说列表
     */
    public List<NovelInfo> listAllNovelByEditorId(Integer editorId) {
        List<NovelPublish> novelPublishList = novelPublishRepo.findAllByEditorId(editorId);
        List<Integer> novelIdList = new ArrayList<>();
        for (NovelPublish np : novelPublishList) {
            novelIdList.add(np.getNovelId());
        }
        Iterable<Novel> novels = novelRepository.findAllById(novelIdList);
        List<NovelInfo> infos = new ArrayList<>();
        for (Novel novel : novels) {
            infos.add(novelToNovelInfo(novel));
        }
        return infos;
    }

    /**
     * 获取小说信息
     */
    public NovelInfo findNovelInfo(Integer novelId) {
        return novelToNovelInfo(novelRepository.findNovelById(novelId));
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
     * 获取热门小说
     * 点击数 + 收藏数 * 2
     */
    public List<NovelInfo> findAllHotNovel() {

        List<Novel> novels = CollectionUtil.iterableToList(novelRepository.findAll());

        class NovelData {
            private Novel novel;
            private Integer favoriteCount = 0;
            private Integer clickCount = 0;
        }
        List<NovelData> datas = novels.stream().map(e -> {
            NovelData data = new NovelData();
            data.novel = e;
            data.favoriteCount = novelInfoService.gainNovelTotalFavorite(e.getId());
            data.clickCount = novelInfoService.gainNovelTotalClick(e.getId());
            return data;
        }).sorted((e1, e2) -> Integer.compare(e2.favoriteCount*2 + e2.clickCount, e1.favoriteCount*2 + e1.favoriteCount))
                .collect(Collectors.toList());

        //截取前四
        int limit = datas.size() < 4?datas.size(): 4;
        return new ArrayList<>(datas.subList(0, limit))
                .stream().map(e->e.novel)
                .map(this::novelToNovelInfo).collect(Collectors.toList());
    }

    /**
     * 获取小说的评论信息列表
     * 条件：novel_id
     *  页面：小说信息页
     */
    public List<NovelCommentInfo> listAllNovelCommentInfo(Integer novelId) {

        List<NovelComment> commentList = novelCommentRepo.findAllByNovelId(novelId);
        List<NovelCommentInfo> infoList = new ArrayList<>();
        for(NovelComment comment: commentList) {
            NovelCommentInfo info = novelCommentToNovelCommentInfo(comment);
            infoList.add(info);

        }
        return infoList;
    }

    /**
     * 获取推荐的小说
     */
    public List<NovelInfo> findAllRecommendNovels(Integer readerId) {

        List<RecommendPriority> priorities = recommendPriorityRepo.findAllByReaderIdOrderByPriorityDesc(readerId);
        List<Integer> novelIds = new ArrayList<>();
        for(RecommendPriority priority: priorities) {
            novelIds.add(priority.getNovelId());
        }
        List<Novel> novelList = CollectionUtil.iterableToList(novelRepository.findAllById(novelIds.subList(0, 3)));
        List<NovelInfo> infos = new ArrayList<>();
        for(Novel novel: novelList) {
            infos.add(novelToNovelInfo(novel));
        }
        return infos;
    }


    /**
     * 获取小说的评论信息列表
     * 条件：reader_id
     * 页面：用户中心
     */
    public List<NovelCommentList> findAllNovelCommentList(Integer readerId) {
        List<NovelComment> commentList = novelCommentRepo.findAllByReaderId(readerId);
        Set<Integer> novelIds = new HashSet<>();
        for(NovelComment comment: commentList) {
            novelIds.add(comment.getNovelId());
        }
        List<Novel> novels = CollectionUtil.iterableToList(novelRepository.findAllById(novelIds));
        List<NovelCommentList> result = new ArrayList<>();
        for(Novel novel: novels) {
            NovelCommentList each = new NovelCommentList();
            each.setNovelId(novel.getId());
            each.setNovelName(novel.getNovelName());
            List<NovelCommentInfo> infoList = new ArrayList<>();
            each.setCommentInfoList(infoList);
            for(NovelComment comment: commentList) {
                if(!comment.getNovelId().equals(novel.getId())) continue;
                NovelCommentInfo info = novelCommentToNovelCommentInfo(comment);
                infoList.add(info);
            }
            infoList.sort((e1, e2)-> (int)e2.getCommentTime().getEpochSecond() - (int)e1.getCommentTime().getEpochSecond());
            result.add(each);
        }
        return result;
    }

    /**
     * 将comment转为commentInfo
     */
    private NovelCommentInfo novelCommentToNovelCommentInfo(NovelComment comment) {
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
        return info;
    }

    /**
     * 将novel转化为novelInfo
     */
    private NovelInfo novelToNovelInfo(Novel novel) {
        Integer authorId = novel.getAuthorId();
        Creator creator = creatorRepository.findById(authorId).orElseThrow();
        NovelInfo novelInfo = new NovelInfo();
        novelInfo.setAuthorId(authorId);
        novelInfo.setBriefIntro(novel.getBriefIntro());
        novelInfo.setPenName(creator.getPenName());
        novelInfo.setId(novel.getId());
        novelInfo.setNovelName(novel.getNovelName());
        novelInfo.setCoverImg(novel.getCoverImg());

        List<Chapter> chapters = chapterRepository.findAllByNovelId(novel.getId());
        novelInfo.setWordCount(chapters.stream().mapToInt(Chapter::getWordCount).sum());

        List<TagNovelRelation> relations = tagNovelRelationRepo.findAllByNovelId(novel.getId());
        List<Integer> tagIds = new ArrayList<>();
        for(TagNovelRelation relation: relations) {
            tagIds.add(relation.getTagId());
        }
        List<Tag> tags = CollectionUtil.iterableToList(tagRepo.findAllById(tagIds));
        novelInfo.setTagList(tags);
        return novelInfo;
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
     * 判断卷是否开放
     */
    public boolean judgeVolumePublished(Integer volumeId) {
        Volume volume = volumeRepository.findById(volumeId).orElseThrow();
        return volume.getStatus().equals(EntityStatus.VOLUME_PASSED);
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
     * 获取章节发布事件
     * 条件：编辑id、指定状态的事件
     */
    public List<EditorWorkEvent> findAllChapterPublishEvent(Integer editorId, Integer status) {
        List<EditorWorkEvent> eventList = new ArrayList<>();

        List<ChapterPublishEvent> publishEventList = chapterPublishEventRepo.findAllByEditorIdAndStatus(editorId, status);

        for(ChapterPublishEvent publishEvent: publishEventList) {
            EditorWorkEvent event = new EditorWorkEvent();
            event.setId(publishEvent.getId());
            event.setAppearTime(publishEvent.getApplyTime().toInstant());
            event.setType(EventType.CHAPTER_PUBLISH_EVENT);
            event.setChapterId(publishEvent.getChapterId());
            event.setAuthorId(publishEvent.getAuthorId());
            event.setAppearTimeStr(LocalDateTime.ofInstant(publishEvent.getApplyTime().toInstant(), ZoneOffset.ofHours(8))
                    .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

            Chapter chapter = chapterRepository.findById(publishEvent.getChapterId()).orElseThrow();
            event.setChapterTitle(chapter.getTitle());
            Novel novel = novelRepository.findById(chapter.getNovelId()).orElseThrow();
            event.setNovelTitle(novel.getNovelName());
            eventList.add(event);
        }
        return eventList;
    }

    /**
     * 同意卷发布
     */
    public void agreeVolumePublish(Integer eventId) {
        VolumePublishEvent event = volumePublishEventRepo.findById(eventId).orElseThrow();
        event.setStatus(EntityStatus.VOLUME_PUBLISH_EVENT_PASSED);
        volumePublishEventRepo.save(event);
        Volume volume = volumeRepository.findById(event.getVolumeId()).orElseThrow();
        volume.setStatus(EntityStatus.VOLUME_PASSED);
        volumeRepository.save(volume);
    }


    /**
     * 同意章节发布
     */
    public void agreeChapterPublish(Integer eventId) {
        ChapterPublishEvent event = chapterPublishEventRepo.findById(eventId).orElseThrow();
        event.setStatus(EntityStatus.CHAPTER_PUBLISH_EVENT_PASSED);
        chapterPublishEventRepo.save(event);
        Chapter chapter = chapterRepository.findById(event.getChapterId()).orElseThrow();
        chapter.setStatus(EntityStatus.CHAPTER_PASSED);
        chapterRepository.save(chapter);
    }

    /**
     * 添加收藏
     */
    public void addFavoriteRela(Integer novelId, Integer readerId) {
        Favorite favorite = new Favorite();
        favorite.setNovelId(novelId);
        favorite.setReaderId(readerId);
        favoriteRepo.save(favorite);
    }

    /**
     * 移除收藏
     */
    public void removeFavoriteRela(Integer novelId, Integer readerId) {
        Favorite favorite = favoriteRepo.findByNovelIdAndReaderId(novelId, readerId).orElseThrow();
        favoriteRepo.deleteById(favorite.getId());
    }

    /**
     * 判断收藏是否存在
     * 存在返回true
     * 不存在返回false
     */
    public boolean judgeFavoriteStatus(Integer novelId, Integer readerId) {
        Optional<Favorite> result = favoriteRepo.findByNovelIdAndReaderId(novelId, readerId);
        return result.isPresent();
    }

    /**
     * 获取收藏
     * 条件：读者id、分页
     */
    public List<NovelInfo> listFavorite(Integer readerId, Integer offset, Integer length) {
        List<Favorite> favoriteList = favoriteRepo.findAllByReaderId(readerId);
        List<Integer> novelIds = new ArrayList<>();
        for(Favorite favorite: favoriteList) {
            novelIds.add(favorite.getNovelId());
        }
        List<Novel> novels = CollectionUtil.iterableToList(novelRepository.findAllById(novelIds));

        List<NovelInfo> novelInfos = new ArrayList<>();
        for(Novel novel: novels) {
            NovelInfo info = new NovelInfo();
            info.setId(novel.getId());
            info.setCoverImg(novel.getCoverImg());
            info.setNovelName(novel.getNovelName());
            info.setBriefIntro(novel.getBriefIntro());
            info.setAuthorId(novel.getAuthorId());
            novelInfos.add(info);
        }

        //从offset到limit之间的数据，不包括limit
        int limit = offset + length < novelInfos.size() ? offset + length : novelInfos.size();

        return novelInfos.subList(offset, limit);
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
     * 通过chapterId获取novelId
     */
    public Integer findNovelIdByChapterId(Integer chapterId) {
        return chapterRepository.findById(chapterId).orElseThrow().getNovelId();
    }


    /**
     * 获取当前编辑推荐的小说
     * 前四本
     */
    public List<NovelInfo> findCurrentEditorRecommendNovels() {

        List<EditorRecommend> recommends = editorRecommendRepo.findAllByStatusOrderByRecommendTimeDesc(EventStatus.EDITOR_RECOMMEND_PASSED);
        Set<Integer> novelIds = new HashSet<>();
        for(EditorRecommend recommend: recommends) {
            if(novelIds.size() == 4) break;
            novelIds.add(recommend.getNovelId());
        }
        return  CollectionUtil
                .iterableToList(novelRepository.findAllById(novelIds))
                .stream()
                .map(this::novelToNovelInfo).collect(Collectors.toList());
    }


    /**
     * 获取新开放小说
     * 前四本
     */
    public List<NovelInfo> findNewPublishNovels() {
        List<NovelPublish> novelPublishList = CollectionUtil.iterableToList(novelPublishRepo.findAll())
                .stream().filter(e -> Objects.nonNull(e.getPublishTime()))
                .sorted((e1, e2) -> Long.compare(e2.getPublishTime().toInstant().getEpochSecond(), e1.getPublishTime().toInstant().getEpochSecond()))
                .collect(Collectors.toList());
        if(novelPublishList.isEmpty()) return new ArrayList<>();
        int limit = novelPublishList.size() < 4?novelPublishList.size(): 4;
        List<Integer> novelIds = new ArrayList<>(novelPublishList.subList(0, limit)).stream().map(NovelPublish::getNovelId).collect(Collectors.toList());
        List<Novel> novels = CollectionUtil.iterableToList(novelRepository.findAllById(novelIds));
        return novels.stream().map(this::novelToNovelInfo).collect(Collectors.toList());

    }



}

