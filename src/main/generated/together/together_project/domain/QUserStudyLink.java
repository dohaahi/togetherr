package together.together_project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserStudyLink is a Querydsl query type for UserStudyLink
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserStudyLink extends EntityPathBase<UserStudyLink> {

    private static final long serialVersionUID = 1779665140L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserStudyLink userStudyLink = new QUserStudyLink("userStudyLink");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QUser participant;

    public final EnumPath<UserStudyJoinStatus> status = createEnum("status", UserStudyJoinStatus.class);

    public final QStudy study;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QUserStudyLink(String variable) {
        this(UserStudyLink.class, forVariable(variable), INITS);
    }

    public QUserStudyLink(Path<? extends UserStudyLink> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserStudyLink(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserStudyLink(PathMetadata metadata, PathInits inits) {
        this(UserStudyLink.class, metadata, inits);
    }

    public QUserStudyLink(Class<? extends UserStudyLink> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.participant = inits.isInitialized("participant") ? new QUser(forProperty("participant")) : null;
        this.study = inits.isInitialized("study") ? new QStudy(forProperty("study"), inits.get("study")) : null;
    }

}

