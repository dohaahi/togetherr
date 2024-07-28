package together.together_project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewCommentLikeLink is a Querydsl query type for ReviewCommentLikeLink
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewCommentLikeLink extends EntityPathBase<ReviewCommentLikeLink> {

    private static final long serialVersionUID = -1303570348L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewCommentLikeLink reviewCommentLikeLink = new QReviewCommentLikeLink("reviewCommentLikeLink");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QReviewComment reviewComment;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUser user;

    public QReviewCommentLikeLink(String variable) {
        this(ReviewCommentLikeLink.class, forVariable(variable), INITS);
    }

    public QReviewCommentLikeLink(Path<? extends ReviewCommentLikeLink> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewCommentLikeLink(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewCommentLikeLink(PathMetadata metadata, PathInits inits) {
        this(ReviewCommentLikeLink.class, metadata, inits);
    }

    public QReviewCommentLikeLink(Class<? extends ReviewCommentLikeLink> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reviewComment = inits.isInitialized("reviewComment") ? new QReviewComment(forProperty("reviewComment"), inits.get("reviewComment")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

