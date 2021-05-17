ToMap

# ToMap

*ToMap* is a method for classifying terms among categories by comparing the syntactic structure of the term and labels of the categories.

This task is useful to normalize named entities or extend existing terminologies.
For instance *ToMap* is used in conjunction with a term extractor to detect named entities and attribute them to categories in a large ontology.

## Definitions

The term to be classified is called the **candidate** term. Labels associated to categories are called **proxy** terms.

Both candidate and proxy terms are assumed to have known syntactic structures.
The syntactic structure of a term comprises a sequence of **tokens** and a tree of **components**.

Each token has three attributes: the **surface form**, the **POS tag** and the **lemma**.

The component tree leaves are the tokens.
Each internal component has *exactly one* **head** subcomponent (sometimes called *governor*) and and *zero to many* **modifier** subcomponents (cometimes called *dependent*).

In a term, the token retrieved by recursively walking down the head is named the **head token**.

### Significant components and significant token heads

The **significant components** are the largest subcomponents that have a head token that is not part of the **graylist**.
The graylist is a user-provided resource parameter (see algorithm parameters).

The algorithm traverses the term syntactic tree from top to bottom and stops when it finds a head not in the graylist.

A term may have no significant component, for instance if all token heads are listed in the graylist.

A term may have several significant components in several ways:
- the term has several significant modifiers;
- the term has at least a significant modifier, and a significant modifier of the head component (see algorithm parameters).

The head token of the significant component is called the **significant head token**.

## Algorithm


### Step 1: Exact match

If the candidate term matches exactly a proxy term, then the candidate term is assigned the same identifier as the matched proxy term.

### Step 2: Same head proxies

The algorithm searches for proxy terms that share at least one significant head token with the candidate term.

If no proxy term shares a significant head with the candidate significant heads, then the candidate is rejected.
Candidate terms that have no significant head are thus rejected.

### Step 3: Similarity

A similarity is computed between the candidate term and each proxy term that shares a significant head token.

The output of the algorithm is a set of attributions that contains the following information:

- the proxy term that shares a significant head token;
- the shared significant head token;
- the category identifier associated with the proxy;
- the token head of the candidate;
- the similarity between the candidate term and the proxy term.

## Algorithm parameters

### Term/token equality

Term equality is used in the following steps:
- exact match;
- token lookup in the graylist;
- significant head token match.

Two terms are equal iff the sequence of tokens is equal.

Token equality has different levels. Two tokens are equal, iff:

- `strict`: their form, lemma and POS-tag are equal. If the lemma is missing in one of the tokens, then the two tokens are not equal.
- `lemma` (recommended): their lemma is equal. If the lemma is missing in one of the tokens, then the two tokens are not equal.
- `lemma default`: their lemma is equal. If the lemma is missing from one term, then it is assumed to be equal to the form.
- `form`: their surface form is equal.

### Graylist

The graylist is a set of heads that bear no significance with respect to the term classification task.
The graylist depends on the terms, topic, and categories.

The graylist is not a blacklist: terms containing graylisted heads may still be classified.
Think of it as a list of tokens that will be ignored in *Step 2*.

### Significant component lookup

The search for a significant component can walk down modifiers only, or head and modifiers.

It is recommended to search through modifiers and head for both candidate terms and proxy terms.

### Term distance

The distance metric used by default is the Jaccard index.
The Jaccard index between two terms is the number of common tokens divided by the set of different tokens in both terms.

#### Stop-words

A list of stop words can be provided.
Tokens equal to any stop-word are completely ignored in the distance.

#### Distance extension

The distance can be computed between the whole term, or only the significant component.

The recommended setting is to compute the similarity between the significant component of the candidate and the whole proxy term.
