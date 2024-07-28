package together.together_project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStudyPostCommentLikeLink is a Querydsl query type for StudyPostCommentLikeLink
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyPostCommentLikeLink extends EntityPathBase<StudyPostCommentLikeLink> {

    private static final long serialVersionUID = -169966133L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStudyPostCommentLikeLink studyPostCommentLikeLink = new QStudyPostCommentLikeLink("studyPostCommentLikeLink");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QStudyPostComment studyPostComment;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUser user;

    public QStudyPostCommentLikeLink(String variable) {
        this(StudyPostCommentLikeLink.class, forVariable(variable), INITS);
    }

    public QStudyPostCommentLikeLink(Path<? extends StudyPostCommentLikeLink> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStudyPostCommentLikeLink(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStudyPostCommentLikeLink(PathMetadata metadata, PathInits inits) {
        this(StudyPostCommentLikeLink.class, metadata, inits);
    }

    public QStudyPostCommentLikeLink(Class<? extends StudyPostCommentLikeLink> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.studyPostComment = inits.isInitialized("studyPostComment") ? new QStudyPostComment(forProperty("studyPostComment"), inits.get("studyPostComment")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

