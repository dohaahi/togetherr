package together.together_project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewLikeLink is a Querydsl query type for ReviewLikeLink
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewLikeLink extends EntityPathBase<ReviewLikeLink> {

    private static final long serialVersionUID = -1489961971L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewLikeLink reviewLikeLink = new QReviewLikeLink("reviewLikeLink");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QReviewPost reviewPost;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QUser user;

    public QReviewLikeLink(String variable) {
        this(ReviewLikeLink.class, forVariable(variable), INITS);
    }

    public QReviewLikeLink(Path<? extends ReviewLikeLink> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewLikeLink(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewLikeLink(PathMetadata metadata, PathInits inits) {
        this(ReviewLikeLink.class, metadata, inits);
    }

    public QReviewLikeLink(Class<? extends ReviewLikeLink> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reviewPost = inits.isInitialized("reviewPost") ? new QReviewPost(forProperty("reviewPost"), inits.get("reviewPost")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

