Activity diagrams provided to this plug-in must follow a certain pattern. Plus, only a subset of UML is allowed.

## Papyrus ##
Activity Diagrams are designed using the Papyrus editor.  Activity Diagrams are modeled in the context of a UML model.

## Classes ##
  * Each C module is described by a **Class**. The global variables are represented by properties and the functions by operations. Macros and constants are **ReadOnly** properties.
  * C structures defined in a module can be declared as **Datatypes** having the corresponding properties.
  * Properties types can be:
    * Datatypes if C type corresponds to a user-defined type
    * Primitive Types if C type is primitive
    * Constant size **Sequences** (NonUnique, Ordered collections of multiplicity n..n) if C type is a constant size array
  * Properties can not be static
  * If a module accesse a propery from an extern module, it should import this module using an **ElementImport**.

## Operations ##
  * An operation can have **In** parameters, **Out** parameters and no more than one **Return** parameter.
  * Parameters can be of primitive types, datatypes or constant sequences.
  * **Inout** parameters are not allowed. To simulate Inout parameters, two parameters x\_in x\_out are declared having directions In and OUT respectively. To use the parameter in an expression, a local variable is introduced having its last value and used in the expression. At the end of the activity, x\_out takes the last value taken by the parameter.
  * If an operation has a side effect on a global variable, it must have an **OwnedRule** containing the corresponding property in its **ConstrainedElements** field.
  * Two operations can not share the same name even if they have different signatures.

## Activities ##
  * The **Specifictaion** field of the activity is set to the operation it describes.
  * For each parameter of the operation, the activity has an **ActivityParameterNode** with the same name, type and direction. The return parameter must be named **result**.
  * An activity has one **InitialNode** and one **ActivityFinaleNode**. The **ControlFlow** starts at the initial node. At the end of the activity, all ControlFlows must meet via a **MergeNode** at the final node.

## Expressions and values ##
  * Expressions and values used in the diagram can either come from an **OutputPin**, an **ActivityParameterNode** or be specified in OCL with **ValuePins**. In the first case, the OutputPin can be owned by the following elements :
    * A **ValueSpecificationAction** : Allows definition of any OCL expression
    * A **ReadVaribaleAction** : Allows access to a variable value
    * A **CallOperatuonAction** : Allows access to call ouputs and result
    * An **OpaqueAction** : Allows access to the result of the action
    * A **ReadStructuralFeatureAction** : Allows access to a property or property field

## Operation calls ##
  * To call an operation, the InputPins of the **CallOperationAction** must have the same name and types that the operation IN parameters. It is the case by default when the element is initialized. An **ObjectFlow** must provide the correct values taken by the parameters.
  * The OutputPins must have the same names and types as the Out parameters. Plus, the return OutputPin must be named **result**.

## Decision Nodes ##
Guards are expressed in OCL. **Decision Nodes** can represent a conditional statement in this case it only creates two new control flows having the condition and its negation as guards or a switch statement and in this case it creates as much control flows as there are switch cases in the C code.

## Merge Node ##
  * **Merge Nodes** merge Control Flows when needed.
  * At the end of the activity, if the activity has multiple branches, all control flows must be merged before incoming to the final node.

## Value Specification ##
  * A **ValueSpecificationAction** has an Outputpint and a value expressed in OCL
  * It is strictly equivalent to specifiy a value by a ValueSpecificationAction and to specify it in a Valuepin. However, it is not always possible to use ValuePins.

## In Parameter ##
An IN activity parameter can be used by different elements, therefore the outgoing object flow must be forked using a **ForkNode**.

## Property read ##
  * We can access class properties and objects fields using **ReadStructuralFeactureActions**.
  * The **Object** InputPin must be empty if we access class properties, otherwise the object must be specified using a ValuePin or an InputPin.
  * To access a collection item, a **local precondition** needs to be added to the action specifying the index preceded by the annotation **@index**.

## Property write ##
  * We can write class properties and objects fields using **AddStructuralFeatureValueAction**
  * To modifiy a collection item, the **insertAt** pin takes the value of the index. Otherwise it must be empty.
  * The **Object** InputPin must be empty if we access class properties, otherwise the object must be specified using a ValuePin or an InputPin.

## Variables ##
Activities can have local variables defined in the **Variables** field.
Variables can be read and written by **ReadVariableActions** and **AddVariableValueActions**. Their fields can be accessed by Read/AddStructuralFeatureActions having the variable as object and the field as StructuralFeature.

## Opaque Actions ##
  * **Opaque Actions** can be introduced to express atomic C instructions.
  * Opaque Actions have InputPins for inputs and an OutPutPin for its unique output.
  * The action behavior is specified in a **local postcondition** expressed in OCL describing the output in terms of the inputs.

## Forbidden names in the model ##
Some names used for internal computations are forbidden in the model.
  * If there exists an operation f in the model, there should not be a class named _f\_context, f\_in or f\_out_.
  * Only simulated Inout parameters can have names ending in `_`in or `_`out
  * If there exists two operations f and g, there should not be a property called _f\_g\_context, g\_f\_context, size\_f\_g\_context or size\_g\_f\_context_
  * The interface names _OperationContext, In and Out_ are forbidden
  * The operation name _ocl\_set_ is forbidden