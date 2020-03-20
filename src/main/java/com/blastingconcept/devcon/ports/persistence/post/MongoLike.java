package com.blastingconcept.devcon.ports.persistence.post;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
@Builder(access = AccessLevel.PUBLIC, toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MongoLike {

    String id;
    String user;
}
