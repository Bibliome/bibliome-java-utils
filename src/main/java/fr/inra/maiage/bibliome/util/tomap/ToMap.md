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


## 
