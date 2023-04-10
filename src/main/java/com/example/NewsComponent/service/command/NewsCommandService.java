package com.example.NewsComponent.service.command;

import com.example.NewsComponent.domain.News;
import com.example.NewsComponent.domain.edge.*;
import com.example.NewsComponent.domain.vertex.File;
import com.example.NewsComponent.domain.vertex.NewsComments;
import com.example.NewsComponent.dto.request.CommentRequest;
import com.example.NewsComponent.dto.request.FileDto;
import com.example.NewsComponent.dto.request.FileInputWithPart;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.Status;
import com.example.NewsComponent.mapper.FileResponseMapper;
import com.example.NewsComponent.mapper.NewsRequestResponseMapper;
import com.example.NewsComponent.metadata.EdgeName;
import com.example.NewsComponent.metadata.VertexName;
import com.example.NewsComponent.repository.NewsRepository;
import com.example.NewsComponent.repository.edge.*;
import com.example.NewsComponent.repository.vertex.FileRepository;
import com.example.NewsComponent.repository.vertex.NewsCommentsRepository;
import com.example.NewsComponent.service.external.NotificationService;
import com.example.NewsComponent.service.external.RewardService;
import com.example.NewsComponent.service.external.UserService;
import com.example.NewsComponent.service.helper.FileDtoService;
import com.example.NewsComponent.service.transaction.Action;
import com.example.NewsComponent.service.transaction.TransactionalWrapper;
import com.example.NewsComponent.dto.request.NewsRequest;
import com.example.NewsComponent.dto.internal.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.NewsComponent.metadata.EdgeName.*;
import static com.example.NewsComponent.metadata.ExternalVertex.USER;
import static com.example.NewsComponent.metadata.VertexName.*;
import static com.example.NewsComponent.utils.FileCombinatorUtils.getAllFilesToSave;

@Service
public class NewsCommandService {

    private final NewsRepository newsRepository;
    private final FileDtoService fileDtoService;
    private final NewsHasInterestRepository newsHasInterestRepository;
    private final NewsHasHashTagRepository newsHasHashTagRepository;
    private final NotificationService notificationService;
    private final TransactionalWrapper transactionalWrapper;
    private final NewsIsForLocationRepository newsIsForLocationRepository;
    private final FileRepository fileRepository;
    private final NewsRequestResponseMapper newsRequestResponseMapper;
    private final NewsHasFileRepository newsHasFileRepository;
    private final FileResponseMapper fileResponseMapper;
    private final UserService userService;
    private final NewsLikedByRepository newsLikedByRepository;
    private final NewsHasCommentRepository newsHasCommentRepository;
    private final NewsCommentsRepository newsCommentsRepository;
    private final CommentHasReplyRepository commentHasReplyRepository;
    private final NewsSharedByRepository newsSharedByRepository;
    private final RewardService rewardService;

    @Value("${reward.service.onLikeNews}")
    private String rewardServiceEndPointOnLikeNews;

    @Value("${reward.service.onCommentNews}")
    private String rewardServiceEndPointOnCommentNews;

    @Value("${reward.service.onShareNews}")
    private String rewardServiceEndPointOnShareNews;

    public NewsCommandService(NewsRepository newsRepository,
                              FileDtoService fileDtoService,
                              NewsHasInterestRepository newsHasInterestRepository,
                              NewsHasHashTagRepository newsHasHashTagRepository,
                              NotificationService notificationService,
                              TransactionalWrapper transactionalWrapper,
                              NewsIsForLocationRepository newsIsForLocationRepository,
                              FileRepository fileRepository,
                              NewsRequestResponseMapper newsRequestResponseMapper,
                              NewsHasFileRepository newsHasFileRepository,
                              FileResponseMapper fileResponseMapper,
                              UserService userService,
                              NewsLikedByRepository newsLikedByRepository,
                              NewsHasCommentRepository newsHasCommentRepository,
                              NewsCommentsRepository newsCommentsRepository,
                              CommentHasReplyRepository commentHasReplyRepository,
                              NewsSharedByRepository newsSharedByRepository,
                              RewardService rewardService) {
        this.newsRepository = newsRepository;
        this.fileDtoService = fileDtoService;
        this.newsHasInterestRepository = newsHasInterestRepository;
        this.newsHasHashTagRepository = newsHasHashTagRepository;
        this.notificationService = notificationService;
        this.transactionalWrapper = transactionalWrapper;
        this.newsIsForLocationRepository = newsIsForLocationRepository;
        this.fileRepository = fileRepository;
        this.newsRequestResponseMapper = newsRequestResponseMapper;
        this.newsHasFileRepository = newsHasFileRepository;
        this.fileResponseMapper = fileResponseMapper;
        this.userService = userService;
        this.newsLikedByRepository = newsLikedByRepository;
        this.newsHasCommentRepository = newsHasCommentRepository;
        this.newsCommentsRepository = newsCommentsRepository;
        this.commentHasReplyRepository = commentHasReplyRepository;
        this.newsSharedByRepository = newsSharedByRepository;
        this.rewardService = rewardService;
    }


    public NewsResponse saveNewsResponse(NewsRequest newsRequest, FileInputWithPart fileInputWithPart) {
        News newsForSaving = newsRequestResponseMapper.getNewsForSaving(newsRequest);
        FileDto fileDto = fileDtoService.getFileDto(fileInputWithPart);

        List<String> interestIds = newsRequest.getInterestIds();
        List<String> hashtagsIds = newsRequest.getHashTagIds();
        Set<String> locationIds = getLocationIds(newsRequest);

        Action<News> action = (arangoDatabase, transactionId) -> {

            News news = newsRepository.saveNews(arangoDatabase, transactionId, newsForSaving);

            List<NewsIsForLocation> newsIsForCountryList = getNewsIsForCountryList(locationIds, news);
            newsIsForCountryList.forEach(newsIsForLocation ->
                    newsIsForLocationRepository.saveNewsForLocation(arangoDatabase, transactionId, newsIsForLocation));

            List<NewsHasInterest> newsHasInterestList = getNewsHasInterests(news, interestIds);
            newsHasInterestList.forEach(newsHasInterest ->
                    newsHasInterestRepository.saveNewsHasInterestEdge(arangoDatabase, transactionId, newsHasInterest));

            List<NewsHasHashTag> newsHasHashTagList = getNewsHasHashTagList(news, hashtagsIds);
            newsHasHashTagList.forEach(newsHasHashTag ->
                    newsHasHashTagRepository.saveNewsHasHashtagsEdge(arangoDatabase, transactionId, newsHasHashTag));

            List<File> savedFiles =
                    fileRepository.saveFiles(arangoDatabase, transactionId, getAllFilesToSave(fileDto));

            List<NewsHasFile> newsHasFiles = savedFiles.stream().map(file -> {
                NewsHasFile newsHasFile = new NewsHasFile();
                newsHasFile.set_from(news.getArangoId());
                newsHasFile.set_to(file.getArangoId());
                return newsHasFile;
            }).toList();
            newsHasFiles.forEach(newsHasFile -> newsHasFileRepository.saveNewsHasFileEdge(arangoDatabase
                    , transactionId
                    , newsHasFile));
            return news;
        };
        News savedNews = transactionalWrapper.executeInsideTransaction
                (Set.of(VertexName.NEWS,
                        EdgeName.NEWS_HAS_INTEREST,
                        EdgeName.NEWS_HAS_HASHTAG,
                        EdgeName.NEWS_IS_FOR_LOCATION,
                        EdgeName.NEWS_HAS_FILE, FILE), action);

        NewsResponse newsResponse = newsRequestResponseMapper.getNewsResponse(savedNews);
        return fileResponseMapper.getNewsResponseWithFiles(savedNews.getId(), newsResponse);

    }

    public static Set<String> getLocationIds(NewsRequest newsRequest) {
        Set<String> countryIds = newsRequest.getCountryIds();
        Set<String> stateIds = newsRequest.getStateIds();
        Set<String> districtIds = newsRequest.getDistrictIds();
        Set<String> tehsilIds = newsRequest.getTehsilIds();
        Set<String> villageIds = newsRequest.getVillageIds();

        Set<String> locationIds = new HashSet<>();
        locationIds.addAll(countryIds);
        locationIds.addAll(Optional.ofNullable(stateIds).orElse(new HashSet<>()));
        locationIds.addAll(Optional.ofNullable(districtIds).orElse(new HashSet<>()));
        locationIds.addAll(Optional.ofNullable(tehsilIds).orElse(new HashSet<>()));
        locationIds.addAll(Optional.ofNullable(villageIds).orElse(new HashSet<>()));
        return locationIds;
    }


    public static List<NewsIsForLocation> getNewsIsForCountryList(Set<String> locationIds, News news) {
        return locationIds.stream().map(locationId -> {
            NewsIsForLocation newsIsForLocation = new NewsIsForLocation();
            newsIsForLocation.set_from(news.getArangoId());
            newsIsForLocation.set_to(locationId);
            newsIsForLocation.setCreatedDate(LocalDateTime.now());
            newsIsForLocation.setModifiedDate(LocalDateTime.now());
            return newsIsForLocation;
        }).toList();
    }

    public static List<NewsHasHashTag> getNewsHasHashTagList(News newsForSaving, List<String> hashtagsIds) {
        return hashtagsIds.stream().map(hashTagIds -> {
            NewsHasHashTag newsHasHashTag = new NewsHasHashTag();
            newsHasHashTag.set_from(newsForSaving.getArangoId());
            newsHasHashTag.set_to(hashTagIds);
            newsHasHashTag.setCreatedDate(LocalDateTime.now());
            newsHasHashTag.setModifiedDate(LocalDateTime.now());
            return newsHasHashTag;
        }).toList();
    }

    public static List<NewsHasInterest> getNewsHasInterests(News newsForSaving, List<String> interestIds) {
        List<NewsHasInterest> newsHasInterestList = interestIds.stream().map(interestId -> {
            NewsHasInterest newsHasInterest = new NewsHasInterest();
            newsHasInterest.set_from(newsForSaving.getArangoId());
            newsHasInterest.set_to(interestId);
            newsHasInterest.setCreatedDate(LocalDateTime.now());
            newsHasInterest.setModifiedDate(LocalDateTime.now());
            return newsHasInterest;
        }).toList();
        return newsHasInterestList;
    }

    public NewsResponse updateNews(NewsRequest newsRequest, FileInputWithPart fileInputWithPart) {
        News news = newsRepository.getNewsById(newsRequest.getId());
        FileDto fileDto = fileDtoService.getFileDto(fileInputWithPart);
        News newsForSaving = newsRequestResponseMapper.updateNews(news, newsRequest);

        Action<News> action = (arangoDatabase, transactionId) -> {
            News savedNews = newsRepository.updateNews(arangoDatabase, transactionId, newsForSaving);
            List<File> savedFiles =
                    fileRepository.saveFiles(arangoDatabase, transactionId, getAllFilesToSave(fileDto));

            List<NewsHasFile> newsHasFiles = savedFiles.stream().map(file -> {
                NewsHasFile newsHasFile = new NewsHasFile();
                newsHasFile.set_from(news.getArangoId());
                newsHasFile.set_to(file.getArangoId());
                return newsHasFile;
            }).toList();
            newsHasFiles.forEach(newsHasFile -> newsHasFileRepository.saveNewsHasFileEdge(arangoDatabase,
                    transactionId, newsHasFile));
            return savedNews;
        };

        News saveNews = transactionalWrapper.executeInsideTransaction(Set.of(NEWS, NEWS_HAS_FILE, FILE),
                action);
        NewsResponse newsResponse = newsRequestResponseMapper.getNewsResponse(saveNews);
        return fileResponseMapper.getNewsResponseWithFiles(saveNews.getId(), newsResponse);

    }

    public NewsResponse publishNews(String newsId) {
        News getNews = newsRepository.getNewsById(newsId);
        getNews.setNewsStatus(NewsStatus.PUBLISHED);
        Action<News> action = (arangoDatabase, transactionId) ->
                newsRepository.updateNews(arangoDatabase, transactionId, getNews);
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of(NEWS), action);
        return newsRequestResponseMapper.getNewsResponse(savedNews);

    }

    public NewsResponse publishAndNotify(String newsId) {
        News newsById = newsRepository.getNewsById(newsId);

        Location location = getPublishedLocationForNotify(newsById);
        Set<String> languageCodes = newsById.getTitle().keySet();
        String language = String.join(",", languageCodes);
        location.setLang(language);

        Interests interests = new Interests();
        interests.setKeywordName(String.join(",", newsById.getInterestIds()));

        Filter filter = getFilter(location, interests);

        Messages messages = new Messages();
        messages.setContent(newsById.getTitle());

        NotificationRequest notificationRequest = getNotificationRequest(newsId, filter, messages);
        // TODO: 20-03-2023 it
        //notificationService.sendNotification(notificationRequest);
        return publishNews(newsId);
    }

    public static Location getPublishedLocationForNotify(News newsById) {
        Location location = new Location();

        location.setCountryID(String.join(",", newsById.getCountryIds()));
        location.setStateID(String.join(",", newsById.getStateIds()));
        location.setDistrictID(String.join(",", newsById.getDistrictIds()));
        location.setTehsilID(String.join(",", newsById.getTehsilIds()));
        location.setVillageID(String.join(",", newsById.getVillageIds()));
        return location;
    }

    public static Filter getFilter(Location location, Interests interests) {
        Filter filter = new Filter();
        filter.setApp("Apni Kheti");
        filter.setPhone("");
        filter.setInterests(interests);
        filter.setLocation(location);
        return filter;
    }

    public static NotificationRequest getNotificationRequest(String newsId, Filter filter, Messages messages) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setAction(newsId);
        notificationRequest.setPriority("Normal");
        notificationRequest.setFilter(filter);
        notificationRequest.setMessages(messages);
        notificationRequest.setValue("News Details");
        notificationRequest.setRequestID("1");
        notificationRequest.setTranslations(List.of());
        notificationRequest.setPostedBy("ak");
        notificationRequest.setUserImage("");
        return notificationRequest;
    }

    public String deleteNews(String id) {
        News news = newsRepository.getNewsById(id);
        news.setStatus(Status.DELETED);
        Action<News> action = (arangoDatabase, transactionId) ->
                newsRepository.updateNews(arangoDatabase, transactionId, news);
        transactionalWrapper.executeInsideTransaction(Set.of(NEWS), action);
        return "deleted";
    }

    public String deleteFiles(String id) {

        File file = fileRepository.getFile(id);
        file.setStatus(Status.DELETED);
        Action<File> action = (arangoDatabase, transactionId) ->
                fileRepository.updateFile(arangoDatabase, transactionId, file);
        transactionalWrapper.executeInsideTransaction(Set.of(FILE), action);
        return "deleted";
    }

    // TODO: 04-04-2023 count no of likes
    public Boolean likeNews(String newsId) {
        News newsById = newsRepository.getNewsById(newsId);
        String idOfCurrentUser = userService.getIdOfCurrentUser();
        NewsLikedBy newsLikedBy = new NewsLikedBy();
        newsLikedBy.set_from(newsById.getArangoId());
        newsLikedBy.set_to(USER + "/" + idOfCurrentUser);
        Action<Boolean> action = (arangoDatabase, transactionId) -> {
            newsLikedByRepository
                    .saveNewsLikedByEdge(arangoDatabase, transactionId, newsLikedBy);
            triggerRewardServiceOnLike(newsById.getId(), idOfCurrentUser);
            return true;
        };
        transactionalWrapper.executeInsideTransaction(Set.of(NEWS_LIKED_BY), action);

        return true;
    }

    public Boolean saveComment(CommentRequest commentRequest) {
        News newsById = newsRepository.getNewsById(commentRequest.getId());
        String idOfCurrentUser = userService.getIdOfCurrentUser();

        NewsComments newsComments = new NewsComments();
        newsComments.setText(commentRequest.getText());
        newsComments.setCreatedDate(LocalDateTime.now());

        Action<Boolean> action = (arangoDatabase, transactionId) -> {
            NewsComments saveNewsComments = newsCommentsRepository.saveNewsComments(arangoDatabase,
                    transactionId, newsComments);
            NewsHasComment newsHasComment = new NewsHasComment();
            newsHasComment.set_from(newsById.getArangoId());
            newsHasComment.set_to(saveNewsComments.getArangoId());
            newsHasCommentRepository.saveNewsHasCommentEdge(arangoDatabase, transactionId, newsHasComment);

            ReplyGivenByUser replyGivenByUser = new ReplyGivenByUser();
            replyGivenByUser.set_from(saveNewsComments.getArangoId());
            replyGivenByUser.set_to(USER + "/" + idOfCurrentUser);
            commentHasReplyRepository.saveReplyGivenByUser(arangoDatabase, transactionId, replyGivenByUser);
            triggerRewardServiceOnComment(newsById.getId(), idOfCurrentUser);
            return true;
        };
        return transactionalWrapper.executeInsideTransaction(Set.of(NEWS_COMMENTS, NEWS_HAS_COMMENT), action);
    }

    public Boolean saveReplyOnComment(CommentRequest commentRequest) {
        NewsComments commentOnWhichReplyIsGiven = newsCommentsRepository.getComment(commentRequest.getId());
        String idOfCurrentUser = userService.getIdOfCurrentUser();
        NewsComments newsComments = new NewsComments();
        newsComments.setText(commentRequest.getText());
        newsComments.setCreatedDate(LocalDateTime.now());


        Action<Boolean> action = (arangoDatabase, transactionId) -> {
            NewsComments saveNewsReply =
                    newsCommentsRepository.saveNewsComments(arangoDatabase, transactionId, newsComments);

            CommentHasReply commentHasReply = new CommentHasReply();
            commentHasReply.set_from(commentOnWhichReplyIsGiven.getArangoId());
            commentHasReply.set_to(saveNewsReply.getArangoId());

            ReplyGivenByUser replyGivenByUser = new ReplyGivenByUser();
            replyGivenByUser.set_from(saveNewsReply.getArangoId());
            replyGivenByUser.set_to(USER + "/" + idOfCurrentUser);

            commentHasReplyRepository.saveReplyGivenByUser(arangoDatabase, transactionId, replyGivenByUser);
            commentHasReplyRepository.saveCommentHasReplyEdge(arangoDatabase, transactionId, commentHasReply);
            return true;
        };
        return transactionalWrapper.executeInsideTransaction(Set.of(NEWS_COMMENTS, COMMENT_HAS_REPLY), action);
    }

    public Boolean saveNewsSharedBy(String id) {

        News newsById = newsRepository.getNewsById(id);
        String idOfCurrentUser = userService.getIdOfCurrentUser();

        NewsSharedBy newsSharedBy = new NewsSharedBy();
        newsSharedBy.set_from(newsById.getArangoId());
        newsSharedBy.set_to(USER + "/" + idOfCurrentUser);

        Action<Boolean> action = (arangoDatabase, transactionId) -> {
            newsSharedByRepository.saveNewsSharedByEdge(arangoDatabase, transactionId, newsSharedBy);
            triggerRewardServiceOnShare(newsById.getId(), idOfCurrentUser);
            return true;
        };
        transactionalWrapper.executeInsideTransaction(Set.of(NEWS_SHARED_BY), action);
        return true;
    }

    public void triggerRewardServiceOnLike(String newsId, String userId) {
        rewardService.triggerRewardService(newsId, userId,
                userService.getTokenOfCurrentUser(), rewardServiceEndPointOnLikeNews);
    }

    public void triggerRewardServiceOnComment(String newsId, String userId) {
        rewardService.triggerRewardService(newsId, userId,
                userService.getTokenOfCurrentUser(), rewardServiceEndPointOnCommentNews);
    }

    public void triggerRewardServiceOnShare(String newsId, String userId) {
        rewardService.triggerRewardService(newsId, userId,
                userService.getTokenOfCurrentUser(), rewardServiceEndPointOnShareNews);
    }

}
