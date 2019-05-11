package com.hk.service;

import com.hk.entity.*;
import com.hk.po.NovelInfo;
import com.hk.repository.*;
import com.hk.util.CollectionUtil;
import com.hk.util.CosineSimilarity;
import com.hk.util.Tuple2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.util.*;

/**
 * 推荐系统
 * @author smallHK
 * 2019/5/7 22:12
 */
@Service
@EnableTransactionManagement
public class RecommendService {

    private TagRepo tagRepo;

    private TagNovelRelationRepo tagNovelRelationRepo;

    private ReaderRepo readerRepo;

    private FavoriteRepo favoriteRepo;

    private NovelRepository novelRepository;

    private RecommendPriorityRepo recommendPriorityRepo;

    public RecommendService(TagRepo tagRepo,
                            TagNovelRelationRepo tagNovelRelationRepo,
                            ReaderRepo readerRepo,
                            FavoriteRepo favoriteRepo,
                            NovelRepository novelRepository,
                            RecommendPriorityRepo recommendPriorityRepo) {
        this.tagNovelRelationRepo = tagNovelRelationRepo;
        this.tagRepo = tagRepo;
        this.readerRepo = readerRepo;
        this.favoriteRepo = favoriteRepo;
        this.novelRepository = novelRepository;
        this.recommendPriorityRepo = recommendPriorityRepo;
    }



    @Transactional
    public void calculateAllReaderVector() {

        Iterable<Tag> tags = tagRepo.findAll();
        List<Integer> orderedTag = new ArrayList<>();
        for (Tag tag : tags) {
            orderedTag.add(tag.getId());
        }

        List<Reader> readerList = CollectionUtil.iterableToList(readerRepo.findAll());


        //所有读者的向量
        //第一维度为reader_id
        //第二维度为tag_id
        int[][] readerVectors = new int[readerList.size()][];

        //根据每个读者建立向量
        for (int i = 0; i < readerList.size(); i++) {
            Reader reader = readerList.get(i);

            List<Favorite> favorites = favoriteRepo.findAllByReaderId(reader.getId());

            //key为tag_id，val为数量，初始化为1，避免0向量的出现
            int[] tagVector = new int[orderedTag.size()];
            for(int j =0; j< tagVector.length; j++) {
                tagVector[j]++;
            }


            //tag_id
            List<Integer> favoriteTags = new ArrayList<>();

            //找出每个用户收藏的书籍id
            //搜出每个用户所有收藏书的所有tag
            for (Favorite favorite : favorites) {
                List<TagNovelRelation> tagNovelRelations = tagNovelRelationRepo.findAllByNovelId(favorite.getNovelId());
                for (TagNovelRelation relation : tagNovelRelations) {
                    favoriteTags.add(relation.getTagId());
                }
            }

            for (Integer tagId : favoriteTags) {
                int index = orderedTag.indexOf(tagId);
                if (index == -1) throw new RuntimeException("标签不存在！");
                tagVector[index]++;
            }
            readerVectors[i] = tagVector;
        }


        //根据所有的读者的向量计算每个读者相对于所有其他读者的相似度
        //key为reader_id
        //value为对应的其他读者相似度
        //第一维度为reader_id
        //第二维度为（reader_id, similarity）
        List[] relaSimilarity = new List[readerList.size()];
        for (int i = 0; i < readerList.size(); i++) {

            int[] readerVector = readerVectors[i];

            //其他读者相关的相似度
            //Tuple2 的first 为reader
            //second 为相似度
            List<Tuple2<Integer, Double>> otherSim = new ArrayList<>(readerList.size() - 1);

            for (int j = 0; j < readerList.size(); j++) {
                if (i == j) continue;
                double similarity = CosineSimilarity.calculate(readerVector, readerVectors[j]);
                Tuple2<Integer, Double> other = new Tuple2(readerList.get(j).getId(), similarity);
                otherSim.add(other);
            }

            relaSimilarity[i] = otherSim;
        }


        List<Novel> novelList = CollectionUtil.iterableToList(novelRepository.findAll());
        List<Integer> orderNovelList = new ArrayList<>();
        for(Novel novel: novelList) {
            orderNovelList.add(novel.getId());
        }

        //每个读者都具有相对应的相似度
        //针对每个用户，利用相似度计算每本书的优先度
        //第一维度为用户
        //第二维度为书籍
        double [][] priority = new double[readerList.size()][];
        for(int i = 0; i < readerList.size(); i++) {

            double[] eachPriority = new double[orderNovelList.size()];

            //获取其他用户相对于自己的相似度
            List<Tuple2<Integer, Double>> otherSimilarity = relaSimilarity[i];


            //搜出除了自己，所有其他用户收藏的书籍
            for(int j = 0; j < readerList.size(); j++) {
                if(i == j) continue;

                //其他用户的收藏
                List<Favorite> favorites = favoriteRepo.findAllByReaderId(readerList.get(j).getId());

                double readerSim = 0;
                //获取此用户的优先度
                for(Tuple2<Integer, Double> sim: otherSimilarity) {
                    if(sim.getFirst().equals(readerList.get(j).getId())) {
                        readerSim = sim.getSecond();
                    }
                }

                for(Favorite favorite: favorites) {
                    int novelIndex = orderNovelList.indexOf(favorite.getNovelId());
                    eachPriority[novelIndex] += readerSim;
                }

            }

            priority[i] = eachPriority;

        }

        //清楚分析上一次分析结果
        recommendPriorityRepo.deleteAll();

        //分析结果持久化
        for(int i = 0; i < priority.length; i++) {

            for(int j = 0; j < novelList.size(); j++) {
               Integer readerId = readerList.get(i).getId();
                Integer novelId = orderNovelList.get(j);
                Double sim = priority[i][j];
               RecommendPriority rp = new RecommendPriority();
               rp.setNovelId(novelId);
               rp.setReaderId(readerId);
               rp.setPriority(sim);
               recommendPriorityRepo.save(rp);

            }

        }

    }

}
