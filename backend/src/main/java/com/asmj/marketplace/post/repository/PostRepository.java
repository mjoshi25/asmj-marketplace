package com.asmj.marketplace.post.repository;

import com.asmj.marketplace.post.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.*;

public interface PostRepository extends MongoRepository<Post,String>{
    List<Post> findByVendorId(String vendorId);
    List<Post> findByStatus(Post.PostStatus s);
    List<Post> findByTypeAndStatus(String type,Post.PostStatus s);
    @Query("{'$or':[{'title':{'$regex':?0,'$options':'i'}},{'description':{'$regex':?0,'$options':'i'}}], 'status':'PUBLISHED'}")
    List<Post> search(String q);
    @Query("{'$or':[{'title':{'$regex':?0,'$options':'i'}},{'description':{'$regex':?0,'$options':'i'}}], 'type':?1, 'status':'PUBLISHED'}")
    List<Post> searchByType(String q,String type);
}
