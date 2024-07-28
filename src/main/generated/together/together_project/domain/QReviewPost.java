package together.together_project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewPost is a Querydsl query type for ReviewPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewPost extends EntityPathBase<ReviewPost> {

    private static final long serialVersionUID = 1380364924L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewPost reviewPost = new QReviewPost("reviewPost");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final QUser author;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath reviewPicUrl = createString("reviewPicUrl");

    public final QStudy study;

    public final NumberPath<Integer> totalLikeCount = createNumber("totalLikeCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QReviewPost(String variable) {
        this(ReviewPost.class, forVariable(variable), INITS);
    }

    public QReviewPost(Path<? extends ReviewPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewPost(PathMetadata metadata, PathInits inits) {
        this(ReviewPost.class, metadata, inits);
    }

    public QReviewPost(Class<? extends ReviewPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QUser(forProperty("author")) : null;
        this.study = inits.isInitialized("study") ? new QStudy(forProperty("study"), inits.get("study")) : null;
    }

}

