package com.predictry.fisher.service;

import com.predictry.fisher.domain.profile.UserProfile;
import com.predictry.fisher.domain.profile.UserProfileAction;
import com.predictry.fisher.domain.profile.UserProfileItem;
import com.predictry.fisher.domain.util.Helper;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.elasticsearch.index.query.FilterBuilders.andFilter;
import static org.elasticsearch.index.query.FilterBuilders.rangeFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;

/**
 * This class provides services related to user profile which is stored in ElasticSearch.
 */
@Service
@Transactional
public class UserProfileService {

    @Autowired
    private ElasticsearchOperations template;

    /**
     * Save a new user profile item to ElasticSearch.
     *
     * @param userProfileItem is the user profile to save.
     */
    public void save(UserProfileItem userProfileItem) {
        String indexName = userProfileItem.getIndexName();
        if (!template.indexExists(indexName)) {
            template.createIndex(indexName);
        }
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setIndexName(indexName);
        indexQuery.setType(userProfileItem.getTenantId());
        indexQuery.setObject(userProfileItem);
        template.index(indexQuery);
    }

    /**
     * Retrieve user profile.  This method returns user profile items within last 6 months.
     *
     * @param tenantId is the tenant id.
     * @param userId is the user id.
     * @param action is the <code>UserProfileAction</code> such as view, buy, etc.
     * @return an instance of <code>UserProfile</code>.  This method will always return an instance of <code>UserProfile</code>.
     *         If no user profile item is found for that user, <code>UserProfile.getLastAction()</code> will be <code>null</code> and
     *         <code>UserProfile.getItems()</code> will be empty.
     *
     */
    public UserProfile getUserProfile(String tenantId, String userId, UserProfileAction action) {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(6);
        LocalDateTime endDate = LocalDateTime.now();
        List<String> indices = Arrays.asList(Helper.convertToIndices("history", startDate, endDate))
            .stream().filter(s -> template.indexExists(s)).collect(Collectors.toList());
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withIndices(indices.toArray(new String[indices.size()]))
            .withTypes(tenantId)
            .withQuery(new FilteredQueryBuilder(null,
                andFilter(termFilter("userId", userId), termFilter("action", action.name().toLowerCase()))))
            .withSort(new FieldSortBuilder("time").order(SortOrder.DESC))
            .build();
        List<UserProfileItem> userProfiles = template.queryForList(searchQuery, UserProfileItem.class);

        UserProfile userProfile = new UserProfile();
        if (!userProfiles.isEmpty()) {
            userProfile.setLastAction(userProfiles.get(0).getTime());
            userProfiles.forEach(u -> {
                u.setTenantId(tenantId);
                userProfile.addAction(u);
            });
        }
        return userProfile;
    }

    /**
     * Retrieving view or buy history for an user at certain date.  The returned <code>Map</code> can be used to represent
     * the following Json:
     *
     * <p>
     * <code>
     * {
     *     "email": "test1@gmail.com",
     *     "items": ["id1", "id2", "id3", "id4"]
     * }
     * </code>
     * </p>
     *
     * @param tenantId is the tenant id.
     * @param userId is the user id.
     * @param action is the <code>UserProfileAction</code> such as view, buy, etc.
     * @return <code>Map</code> with history data or an empty <code>Map</code> if nothing is found.
     */
    public Map<String,Object> getHistory(String tenantId, String userId, UserProfileAction action, LocalDate date) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withIndices(String.format("history_%d", date.getYear()))
            .withTypes(tenantId)
            .withFilter(andFilter(
                rangeFilter("time").from(date.atStartOfDay()).to(date.plusDays(1).atStartOfDay()),
                termFilter("userId", userId),
                termFilter("action", action.name().toLowerCase())
            ))
            .build();
        List<UserProfileItem> items = template.queryForList(searchQuery, UserProfileItem.class);

        Map<String,Object> results = new HashMap<>();
        if (!items.isEmpty()) {
            results.put("email", items.get(0).getEmail());
            results.put("items", items.stream().map(UserProfileItem::getItemId).collect(Collectors.toList()));
        }
        return results;
    }

}
