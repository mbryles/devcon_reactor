package com.blastingconcept.devcon.ports.persistence.post;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "posts")
@Data
@ToString
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class MongoPost {

    @Id
    private ObjectId id;


    private String userId;
    private String text;
    private String name;
    private String avatar;
    private List<MongoComment> comments;

    private List<String> userLikes;
    private Date date;

}
