package com.github.lateralthoughts.hands_on_neo4j.frontend;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.traversal.Evaluators.toDepth;
import static org.neo4j.helpers.collection.IteratorUtil.asIterable;
import static org.neo4j.kernel.Traversal.description;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.UniqueFactory;

import com.github.lateralthoughts.hands_on_neo4j.domain.*;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.*;
import com.github.lateralthoughts.hands_on_neo4j.framework.datastructures.Entry;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.CommitUtils;
import org.neo4j.kernel.logging.BufferingLogger;

/**
 * <motto>BIRGGIT, because YOLO!</motto>
 * Barely-Implemented and Roughly Graph-Based GIT.
 *
 * @author Florent Biville (@fbiville)
 * @author Olivier Girardot (@ogirardot)
 */
public final class BIRGGIT {

    private static final int DEFAULT_COMMIT_PAGE_SIZE = 25;

    private final GraphDatabaseService graphDB;
    private final LabelFinder labels;
    private final PropertyFinder properties;
    private final RelationTypeFinder relationshipTypes;
    private final UniqueIdentifierFinder uniqueIdentifiers;
    private final IndexFinder indices;
    private final CommitUtils commitUtils;
    private ExecutionEngine engine;

    @Inject
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
        engine = new ExecutionEngine(graphDB, new BufferingLogger());
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
            Relationship relationship = findBranch(branch.getProperty("name").toString());
            if (relationship != null) {
                graphDB.index()
                        .forRelationships(indexName)
                        .remove(relationship,
                                uniqueIdentifiers.findSingleKey(Branch.class),
                                branch.getProperty("name"));
            }
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
     * Find an indexed branch by its name
     */
    public Relationship findBranch(String branchName) {
        checkArgument(branchName != null && !branchName.isEmpty());

        String indexName = indices.findName(Branch.class);
        String indexedKey = uniqueIdentifiers.findSingleKey(Branch.class);

        try (Transaction tx = graphDB.beginTx();
             IndexHits<Relationship> indexResults = graphDB.index()
                 .forRelationships(indexName)
                 .get(indexedKey, branchName)) {

            Relationship result = indexResults.getSingle();
            tx.success();
            return result;
        }
    }

    /**
     * Creates a new {@link Commit} node and updates the provided
     * {@link Branch} relationship accordingly.
     *
     * @return the updated {@link Branch} relationship
     */
    public Relationship commit(Commit commit, Branch currentBranch) {
        checkNotNull(commit);

        try (Transaction tx = graphDB.beginTx()) {
            Relationship branch = findBranch(currentBranch.getName());
            branch.delete();
            createUniqueRelationship(
                new ParentCommit(
                    currentBranch.getCommit(),
                    commit
                )
            );
            Relationship result = createBranch(new Branch(currentBranch, commit));

            tx.success();
            return result;
        }
    }


    /**
     * Merges the second provided branch to the first one.
     *
     * @return the updated {@link Branch} relationship
     */
    public Relationship merge(Branch firstBranch, Branch secondBranch) {
        checkNotNull(firstBranch);
        checkNotNull(secondBranch);

        try (Transaction tx = graphDB.beginTx()) {
            Commit mergeCommit = newMergeCommit(secondBranch);
            Relationship updatedFirstBranch = commit(mergeCommit, firstBranch);
            createUniqueRelationship(
                new ParentCommit(
                    secondBranch.getCommit(),
                    mergeCommit
                )
            );

            tx.success();
            return updatedFirstBranch;
        }
    }

    /**
     * Initializes a BIRGGIT project.
     * Almost prepares t' coffee for ye!
     */
    public void init(Branch branch, Commit... commits) {
        checkNotNull(commits);
        checkArgument(commits.length > 0);

        try (Transaction tx = graphDB.beginTx()) {
            indexBranch(createBranch(branch));

            for (Commit commit : commits) {
                indexBranch(commit(commit, branch));
                branch = new Branch(branch, commit);
            }
            tx.success();
        }
    }

    /**
     * @see {@link this#log(String, int)}
     */
    public Collection<Node> log(String branchName) {
        return log(branchName, DEFAULT_COMMIT_PAGE_SIZE);
    }

    /**
     * Logs all commits of specified branch up to the provided limit
     */
    public Collection<Node> log(String branchName, int limit) {
        checkArgument(limit > 0, "Limit of commits to log should be strictly positive");

        ImmutableList.Builder<Node> logs = ImmutableList.builder();

        try (Transaction tx = graphDB.beginTx()) {
            for (Path position : description().depthFirst()
                .evaluator(toDepth(limit - 1))
                .relationships(
                    relationshipTypes.findRelationshipType(ParentCommit.class),
                    INCOMING
                )
                .traverse(findBranch(branchName).getEndNode())) {

                logs.add(position.endNode());
            }
            tx.success();
        }
        return logs.build();
    }

    /**
     * Finds the latest commits both in first and second branch.
     */
    public Node findCommonAncestor(String firstBranch, String secondBranch) {
        try (Transaction tx = graphDB.beginTx()) {
            RelationshipType relationshipType = relationshipTypes.findRelationshipType(ParentCommit.class);

            Node firstBranchStart = findBranch(firstBranch).getEndNode();
            Node secondBranchStart = findBranch(secondBranch).getEndNode();

            for (Path firstBranchPosition : description()
                .depthFirst()
                .relationships(relationshipType, INCOMING)
                .traverse(firstBranchStart)) {

                for (Path secondBranchPosition : description()
                    .depthFirst()
                    .relationships(relationshipType, INCOMING)
                    .traverse(secondBranchStart)) {

                    if (secondBranchPosition.endNode().getId() == firstBranchPosition.endNode().getId()) {
                        tx.success();
                        return secondBranchPosition.endNode();
                    }
                }
            }
            tx.success();
        }
        return null;
    }

    /**
     * Logs all commits of second branch until a join point with first branch is met.
     */
    public Collection<Node> log(String firstBranch, String secondBranch) {
        Node ancestor = findCommonAncestor(firstBranch, secondBranch);
        if (ancestor == null) {
            return ImmutableList.of();
        }

        ImmutableList.Builder<Node> logs = ImmutableList.builder();
        try (Transaction tx = graphDB.beginTx()) {
            for (Path position : description()
                .depthFirst()
                .evaluator(toDepth(DEFAULT_COMMIT_PAGE_SIZE - 1))
                .relationships(relationshipTypes.findRelationshipType(ParentCommit.class), INCOMING)
                .traverse(findBranch(secondBranch).getEndNode())) {

                Node currentNode = position.endNode();
                logs.add(currentNode);

                if (currentNode.equals(ancestor)) {
                    break;
                }
            }
            tx.success();
        }
        return logs.build();
    }

    /**
     * Finds 1 commit against its provided identifier
     */
    public Node findOneCommit(String uniqueIdentifier) {
        Collection<Node> commits;
        try (Transaction tx = graphDB.beginTx();
             ResourceIterator<Node> result = engine.execute(
                 format("MATCH (commit:COMMIT) WHERE commit.identifier = '%s' RETURN commit", uniqueIdentifier)
             ).javaColumnAs("commit")) {

            commits = newArrayList(asIterable(result));
            tx.success();
        }
        Iterator<Node> iterator = commits.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    /**
     * Deletes a commit against its identifier
     */
    public void delete(String commitIdentifier) {
        try (Transaction tx = graphDB.beginTx()) {
            engine.execute(
                format("MATCH (commit:COMMIT) WHERE commit.identifier = '%s' DELETE commit", commitIdentifier)
            );
            tx.success();
        }
    }

    /**
     * Deletes commits against their identifiers
     */
    public void deleteAll(String... commitIdentifiers) {
        checkNotNull(commitIdentifiers);
        checkArgument(commitIdentifiers.length > 0);

        try (Transaction tx = graphDB.beginTx()) {
            engine.execute(
                format(
                    "MATCH (commit:COMMIT) WHERE commit.identifier IN ['%s'] DELETE commit",
                    Joiner.on("','").join(commitIdentifiers)
                )
            );
            tx.success();
        }
    }

    /**
     * Finds commits that are not connected to anything
     */
    public Collection<Node> findOrphanCommits() {
        try (Transaction tx = graphDB.beginTx();
             ResourceIterator<Node> orphans = engine.execute(
                 "START orphan=node(*) MATCH (orphan:COMMIT)-[r?]->() WHERE r IS NULL RETURN orphan AS n"
             ).javaColumnAs("n")) {

            Collection<Node> result = newArrayList(asIterable(orphans));
            tx.success();
            return result;
        }
    }

    /**
     * Deletes orphan commits
     */
    public void gc() {
        try (Transaction tx = graphDB.beginTx()) {
            Collection<Node> orphans = findOrphanCommits();
            String[] identifiers = new String[orphans.size()];
            int i = 0;
            for (Node orphan : orphans) {
                identifiers[i++] = orphan.getProperty("identifier").toString();
            }
            deleteAll(identifiers);
            tx.success();
        }
    }


    private Commit newMergeCommit(Branch mergedBranch) {
        return new Commit(
            commitUtils.uniqueIdentifier(),
            format("Merged %s", mergedBranch.getName())
        );
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
