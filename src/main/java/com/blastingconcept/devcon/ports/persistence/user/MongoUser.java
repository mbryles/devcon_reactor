package com.blastingconcept.devcon.ports.persistence.user;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

@Document(collection = "users")
@Data
@ToString
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
//@CompoundIndexes({
//        @CompoundIndex(name = "id_email", def = "{'_id': 1, 'email': -1}", unique = true)
//})
public class MongoUser {

    @Id
    private ObjectId id;

    private String name;

    @Indexed( unique = true)
    private String email;
    private String password;
    private String avatar;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date date;



}

