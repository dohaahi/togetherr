package together.together_project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStudyPostComment is a Querydsl query type for StudyPostComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyPostComment extends EntityPathBase<StudyPostComment> {

    private static final long serialVersionUID = 837487482L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStudyPostComment studyPostComment = new QStudyPostComment("studyPostComment");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final QUser author;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> parentCommentId = createNumber("parentCommentId", Long.class);

    public final QStudyPost studyPost;

    public final NumberPath<Integer> totalLikeCount = createNumber("totalLikeCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QStudyPostComment(String variable) {
        this(StudyPostComment.class, forVariable(variable), INITS);
    }

    public QStudyPostComment(Path<? extends StudyPostComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStudyPostComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStudyPostComment(PathMetadata metadata, PathInits inits) {
        this(StudyPostComment.class, metadata, inits);
    }

    public QStudyPostComment(Class<? extends StudyPostComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QUser(forProperty("author")) : null;
        this.studyPost = inits.isInitialized("studyPost") ? new QStudyPost(forProperty("studyPost")) : null;
    }

}

