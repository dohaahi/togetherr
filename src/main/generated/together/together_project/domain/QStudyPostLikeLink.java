package together.together_project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStudyPostLikeLink is a Querydsl query type for StudyPostLikeLink
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyPostLikeLink extends EntityPathBase<StudyPostLikeLink> {

    private static final long serialVersionUID = 1897248374L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStudyPostLikeLink studyPostLikeLink = new QStudyPostLikeLink("studyPostLikeLink");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QStudyPost studyPost;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUser user;

    public QStudyPostLikeLink(String variable) {
        this(StudyPostLikeLink.class, forVariable(variable), INITS);
    }

    public QStudyPostLikeLink(Path<? extends StudyPostLikeLink> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStudyPostLikeLink(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStudyPostLikeLink(PathMetadata metadata, PathInits inits) {
        this(StudyPostLikeLink.class, metadata, inits);
    }

    public QStudyPostLikeLink(Class<? extends StudyPostLikeLink> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.studyPost = inits.isInitialized("studyPost") ? new QStudyPost(forProperty("studyPost")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

