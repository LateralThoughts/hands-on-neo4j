package com.github.lateralthoughts.hands_on_neo4j.frontend;

import com.github.lateralthoughts.hands_on_neo4j.domain.Branch;
import com.github.lateralthoughts.hands_on_neo4j.domain.Commit;
import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.domain.Project;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.*;
import com.github.lateralthoughts.hands_on_neo4j.framework.datastructures.Entry;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.CommitUtils;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.UniqueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <motto>BIRGGIT, because YOLO!</motto>
 * Barely-Implemented and Roughly Graph-Based GIT.
 *
 * @author Florent Biville (@fbiville)
 * @author Olivier Girardot (@ogirardot)
 */
@Component
public final class BIRGGIT {

    private final GraphDatabaseService graphDB;
    private final LabelFinder labels;
    private final PropertyFinder properties;
    private final RelationTypeFinder relationshipTypes;
    private final UniqueIdentifierFinder uniqueIdentifiers;
    private final IndexFinder indices;
    private final CommitUtils commitUtils;

    @Autowired
    public BIRGGIT(GraphDatabaseService graphDB,
                   LabelFinder labels,
                   PropertyFinder properties,
                   RelationTypeFinder relationshipTypes,
                   UniqueIdentifierFinder uniqueIdentifiers,
                   IndexFinder indices,
                   CommitUtils commitUtils) {

        this.graphDB = graphDB;
        this.labels = labels;
        this.properties = properties;
        this.relationshipTypes = relationshipTypes;
        this.uniqueIdentifiers = uniqueIdentifiers;
        this.indices = indices;
        this.commitUtils = commitUtils;
    }

    /**
     * Creates a new {@link Project} node.
     */
    public Node createNewProject(Project project) {
        checkNotNull(project);

        try (Transaction tx = graphDB.beginTx()) {
            Node node = createUniqueNode(project);
            tx.success();
            return node;
        }
    }

    /**
     * Creates a new {@link Commit} node.
     */
    public Node commit(Commit commit) {
        checkNotNull(commit);

        try (Transaction tx = graphDB.beginTx()) {
            Node node = createUniqueNode(commit);
            tx.success();
            return node;
        }
    }

    /**
     * Amends an existing {@link Commit} node.
     * Note that a new ID will be created.
     */
    public Node amend(Node node, String newMessage) {
        checkNotNull(node);

        try (Transaction tx = graphDB.beginTx()) {
            Node newNode = cloneNode(node);
            newNode.setProperty("message", newMessage);
            tx.success();
            return newNode;
        }
    }

    /**
     * Creates a new {@link Branch} node.
     */
    public Relationship createBranch(Branch newBranch) {
        checkNotNull(newBranch);

        try (Transaction tx = graphDB.beginTx()) {
            Node project = createUniqueNode(newBranch.getProject());
            Node commit = createUniqueNode(newBranch.getCommit());
            Relationship relationship = project.createRelationshipTo(
                commit,
                relationshipTypes.findRelationshipType(newBranch)
            );
            populateProperties(relationship, newBranch);

            tx.success();
            return relationship;
        }
    }

    /**
     * Add a {@link Branch} relationship to the index.
     */
    public void indexBranch(Relationship branch) {
        checkNotNull(branch);

        String indexName = indices.findName(Branch.class);
        try (Transaction tx = graphDB.beginTx()) {
            graphDB.index()
                .forRelationships(indexName)
                .add(
                    branch,
                    uniqueIdentifiers.findSingleKey(Branch.class),
                    branch.getProperty("name")
                );
            tx.success();
        }
    }

    /**
     * TODO
     * Hint: cf {@link this#indexBranch(org.neo4j.graphdb.Relationship)}
     * The code is very similar to what's needed here ;-)
     *
     *  1 - query index in try-block (graphDB.index()....)
     *  2 - return single result
     */
    public Relationship findBranch(String branchName) {
        checkArgument(branchName != null && !branchName.isEmpty());

        String indexName = indices.findName(Branch.class);
        String indexedKey = uniqueIdentifiers.findSingleKey(Branch.class);

        try (Transaction tx = graphDB.beginTx();
             IndexHits<Relationship> indexResults = null/*TODO*/) {

            Relationship result = indexResults.getSingle();
            tx.success();
            return result;
        }
    }

    /**
     * TODO
     *
     *  1 - find branch by name
     *  2 - delete former branch relationship
     *  3 - create parent commit relationship
     *          (currentBranch.commit)-[:PARENT]->(commit)
     *  4 - recreate new branch from commit
     *          ({@link Branch#Branch(Branch, Commit)})
     *
     */
    public Relationship commit(Commit commit, Branch currentBranch) {
        checkNotNull(commit);

        try (Transaction tx = graphDB.beginTx()) {
            Relationship result = null/*TODO*/;

            tx.success();
            return result;
        }
    }


    /**
     * TODO
     *
     * 1 - create new commit (merge commit)
     * 2 - create parent relationship between new commit and 2nd branch HEAD
     *          {@link this#createUniqueRelationship(Domain)}
     * 3 - commit the merge commit on the 1st branch
     */
    public Relationship merge(Branch firstBranch, Branch secondBranch) {
        checkArgument(firstBranch != null);
        checkArgument(secondBranch != null);

        try (Transaction tx = graphDB.beginTx()) {
            Relationship updatedFirstBranch = null/*TODO*/;

            tx.success();
            return updatedFirstBranch;
        }
    }

    private Node createUniqueNode(final Domain entity) {
        final Entry<String, Object> identity = uniqueIdentifiers.findSingleKeyValue(entity);
        final String key = identity.getKey();
        final Object value = identity.getValue();
        UniqueFactory.UniqueNodeFactory uniqueNodeFactory = new UniqueFactory.UniqueNodeFactory(graphDB, indices.findName(entity)) {
            @Override
            protected void initialize(Node newlyCreatedNode, Map<String, Object> properties) {
                newlyCreatedNode.setProperty(key, value);
                populateLabels(newlyCreatedNode, entity);
                populateProperties(newlyCreatedNode, entity);
            }
        };
        return uniqueNodeFactory.getOrCreate(key, value);
    }


    private void populateLabels(Node newlyCreatedNode, Domain entity) {
        for (Label label : labels.findAllLabels(entity.getClass())) {
            newlyCreatedNode.addLabel(label);
        }
    }

    /**
     * Set exposed domain properties on Neo4J {@link PropertyContainer} node/relationship.
     */
    private PropertyContainer populateProperties(PropertyContainer propertyContainer, Domain entity) {
        for (Entry<String, Object> entityProperties : this.properties.findAll(entity)) {
            propertyContainer.setProperty(entityProperties.getKey(), entityProperties.getValue());
        }
        return propertyContainer;
    }

    private Node cloneNode(Node formerNode) {
        Node clone = graphDB.createNode(labels.findAllLabels(Commit.class));
        setProperties(formerNode, clone);
        overrideIdentifier(formerNode, clone);
        reassignRelationships(formerNode, clone);
        return clone;
    }

    private void setProperties(Node formerNode, Node clone) {
        for (String key : formerNode.getPropertyKeys()) {
            clone.setProperty(key, formerNode.getProperty(key));
        }
    }

    private void overrideIdentifier(Node formerNode, Node clone) {
        String newId = String.valueOf(formerNode.getProperty("identifier")) + "_amended";
        clone.setProperty("identifier", newId);
    }

    private void reassignRelationships(Node formerNode, Node clone) {
        for (Relationship relationship : formerNode.getRelationships()) {
            RelationshipType type = relationship.getType();
            if (relationship.getStartNode().equals(formerNode)) {
                clone.createRelationshipTo(relationship.getEndNode(), type);
            } else {
                relationship.getEndNode().createRelationshipTo(clone, type);
            }
            relationship.delete();
        }
    }

    private Relationship createUniqueRelationship(final Domain relationshipEntity) {
        UniqueFactory.UniqueRelationshipFactory uniqueRelationshipFactory = new UniqueFactory.UniqueRelationshipFactory(graphDB, indices.findName(relationshipEntity)) {
            @Override
            protected Relationship create(Map<String, Object> properties) {
                Node startNode = createUniqueNode(relationshipTypes.findStart(relationshipEntity));
                Node endNode = createUniqueNode(relationshipTypes.findEnd(relationshipEntity));
                Relationship relationship = startNode.createRelationshipTo(
                    endNode,
                    relationshipTypes.findRelationshipType(relationshipEntity)
                );

                for (Map.Entry<String, Object> keyValue : properties.entrySet()) {
                    relationship.setProperty(keyValue.getKey(), keyValue.getValue());
                }
                return relationship;
            }
        };

        Entry<String, Object> identity = uniqueIdentifiers.findSingleKeyValue(relationshipEntity);
        return uniqueRelationshipFactory.getOrCreate(identity.getKey(), identity.getValue());
    }
}
