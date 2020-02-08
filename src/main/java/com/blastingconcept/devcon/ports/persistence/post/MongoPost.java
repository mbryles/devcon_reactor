package com.blastingconcept.devcon.ports.persistence.post;

import com.blastingconcept.devcon.ports.persistence.user.MongoUser;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "posts")
@Data
@ToString
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "user_text", def = "{'user': 1, 'text': -1}", unique = true)
})
public class MongoPost {

    @Id
    private ObjectId id;

    @DBRef
    private MongoUser user;

    private String text;
    private String name;
    private String avatar;

    @DBRef
    private List<MongoUser> userLikes;
    private Date date;

}
