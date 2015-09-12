form-binder-java
=================
[![Build Status](https://travis-ci.org/tminglei/form-binder-java.svg?branch=master)](https://travis-ci.org/tminglei/form-binder-java)


Form-binder-java is java port of [form-binder](https://github.com/tminglei/form-binder), a micro data binding and validating framework.



Features
-------------
- very lightweight, only ~1500 lines codes (framework + built-in extensions)
- easy use, no verbose codes, and what you see is what you get
- high customizable, you can extend almost every executing point
- easily extensible, every extension interface is an alias of `FunctionN`
- immutable, you can share mapping definition object safely



Usage
-------------
To use `form-binder-java`, pls add the dependency to your `maven` project file:
```xml
<dependency>
  <groupId>com.github.tminglei</groupId>
  <artifactId>form-binder-java</artifactId>
  <version>0.11.0</version>
</dependency>
```


Then you can use it in your codes like this:

![form-binder description](https://github.com/tminglei/form-binder-java/raw/master/form-binder-desc.png)

1. define your binder
2. define your mappings
3. prepare your data
4. bind and consume


> _p.s. every points above (1)/(2)/(3)/(4)/ are all extendable and you can easily customize it._  



How it works
--------------------
### Principle
The core of `form-binder` is `Mapping`, **tree structure** mappings. With **depth-first** algorithm, it was used to validate data and construct the result value object.

### Details

![form-binder description](https://github.com/tminglei/form-binder-java/raw/master/form-binder-desc.png)

#### Major Components:  
[1] **binder**: facade, used to bind and trigger processing, two major methods: `bind`, `validate`  
[2] **messages**: used to provide error messages  
[3] **mapping**: holding constraints, processors, and maybe child mapping, etc. used to validate/convert data, two types of mappings: `field` and `group`  
[4] **data**: inputting data map  

> _Check [here](https://github.com/tminglei/form-binder-java/blob/master/src/main/java/com/github/tminglei/bind/Framework.java) for framework details._

binder **bind** method signature (return an `BindObject` and let user to continue processing):
```java
//bind mappings to data, and return an either, holding validation errors or converted value
public BindObject bind(Framework.Mapping<?> mapping, Map<String, String> data, String root)
```

binder **validate** method signature (_validate only_ and not consume converted data):
```java
//return (maybe processed) errors
public <Err> Optional<Err> validate(Framework.Mapping<?> mapping, Map<String, String> data, String root)
```

> _Check [here](https://github.com/tminglei/form-binder-java/blob/master/src/main/java/com/github/tminglei/bind/Mappings.java) for built-in **mapping**s._  

#### Extension Types:  
(1) **ErrProcessor**: used to process error seq, like converting it to json  
(2) **PreProcessor**: used to pre-process data, like omitting `$` and `,` from `$3,013`  
(3) **Constraint**: used to validate raw string data  
(4) **ExtraConstraint**: used to valdate converted value  

> _* Check [here](https://github.com/tminglei/form-binder-java/blob/master/src/main/java/com/github/tminglei/bind/Processors.java) for built-in `PreProcessor`/`ErrProcessor`._  
> _**Check [here](https://github.com/tminglei/form-binder-java/blob/master/src/main/java/com/github/tminglei/bind/Constraints.java) for built-in `Constraint`/`ExtraConstraint`._

#### Options/Features:  
1) **label**: `feature`, readable name for current group/field  
2) **mapTo**: `feature`, map converted value to another type  
3) **i18n**: `option`, let label value can be used as a message key to fetch a i18n value from `messages`   
4) **eagerCheck**: `option`, check errors as more as possible  
5) **ignoreEmpty**: `option`, not check empty field/values, especially they're not touched by user  
6) **touched**: `function`, check whether a field was touched by user; if yes, they can't be empty if they're required  

> _* By default, `form-binder-java` would return right after encountered a validation error._  
> _** ignoreEmpty + touched, will let form-binder re-check touched empty field/values._  
> _*** if i18n is on, the label you input should be a message key instead of a value._

#### Extensible object and meta info
If you want to associate some extra data to a mapping, now you can do it like this:
```java
Mapping<BindObject> pet = mapping(
    field("id", vLong().$ext(o -> ext(o).desc("pet id"))),
    field("name", text(required()).$ext(o -> ext(o).desc("pet name"))),
    field("category", attach(required()).to(mapping(
        field("id", vLong(required())),
        field("name", text(required()))
    )).$ext(o -> ext(o).desc("category belonged to"))),
    field("photoUrls", list(text()).$ext(o -> ext(o).desc("pet's photo urls"))),
    field("tags", list(text()).$ext(o -> ext(o).desc("tags for the pet"))),
    field("status", petStatus)
).$ext(o -> ext(o).desc("pet info"));
```
> with this and meta info, which can be fetched from a `Mapping` / `PreProcessor` / `Constraint` / `ExtraConstraint` with `[instance].meta()`, `form-binder-java` allows third party tools, like [binder-swagger-java](https://github.com/tminglei/binder-swagger-java), to deeply know its structure and details, then based on it to do more, just like they based on java reflections.


_p.s. for more dev and usage details pls check the [source codes](https://github.com/tminglei/form-binder-java/tree/master/src/main/java/com/github/tminglei/bind) and [test cases](https://github.com/tminglei/form-binder-java/tree/master/src/test/java/com/github/tminglei/bind)._



How to
--------------------
[TODO]



Build & Test
-------------------
To hack it and make your contribution, you can setup it like this:
```bash
 $ git clone https://github.com/tminglei/form-binder.git
 $ cd form-binder
 $ sbt
...
```
To run the tests, pls execute:
```bash
 $ sbt test
```



License
---------
The BSD License, Minglei Tu &lt;tmlneu@gmail.com&gt;
