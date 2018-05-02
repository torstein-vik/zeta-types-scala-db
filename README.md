# Zeta Types (Scala) Database
***Database bindings for multiplicative functions / zeta-types*** <p>
[![Build Status](https://travis-ci.org/torstein-vik/zeta-types-scala.svg?branch=master)](https://travis-ci.org/torstein-vik/zeta-types-scala-db)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)


## Installation

1. Clone this repo to your computer
2. Make sure you have SBT (simple build tool) installed on your computer
3. Run 'sbt' in the main directory to start SBT
4. Inside SBT, run 'test' to test the codebase 
5. Run 'test:console' to get an interactive console

Instructions for the actual database setup is not available or decided yet.

Please tell us if this doesn't work, because that means something is wrong with our instructions.

## Current features

TODO...
### Query system
Queries are built from properties and predicates. A query starts with a list of properties (separated w/ ~) that are the outputs from the query. In order to restrict the set of returned multiplicative functions, continue with "where (...)" where ... is some Predicate. 

#### Properties
All properties have a type, which is the kind of object they represent. Constants are automatically properties. Given an Option[T]-property, which works like optional part of the db-schema of type T, one can write ".get" to turn it into a property of type T. However, if the value doesn't exist, the query will crash.

Here are the currently available properties that come from multiplicative functions:

- mf of type MultiplicativeFunction: the entire multiplicative function
- mflabel of type String: the mf-label
- batchid of type Option[String]: the batchId
- name of type String: the descriptiveName
- belltable of type String: a string containing a nicely formatted bell-table with the label, name, and definition
- definition of type String: the verbalDefinition
- comments of type Seq[String]: the list of comments
- properties of type Seq[String]: the list of properties with value true

- mfvalue(n) of type Option[ComplexNumber]: the value of the function at the natural number n
- mfbell(p, e) of type Option[ComplexNumber]: the value of the function at the natural number p^e with p prime

#### Predicates
Predicates are built from properties. For any two properties, placing "===" or "!==" between them will create a predicate based on the two being equal or different respectively. Predicates can also be combined using "and"/"&" and "or"/"|", as well as negated using ".not" or by prepending "!". 

Furthermore, certain types of properties have additional functionality to create predicates. 

- Any option-type can use ".exists" to check if the value exists. Additionally, "==?" and "!=?" are provided to check for existence before checking equality or inequality.
- Any string-type can use "contains" to check if some other string-property (including constants) is contained in this string. 
- Any string-type can use 'regex "...".r' to check if this string matches some regular expression. 
- Any seq-type can use "contains" to check if some property value is contained in the sequence.
- Any seq-type can use "has" and "all" to check if some predicate holds for respectively at least one or all of the elements in the sequence. This is specified by a property-function, such as "_ contains ..." or "_ === 20".

## Usage Examples

TODO...

## Contributors

_Ask Torstein ([torsteinv64@gmail.com](mailto:torsteinv64@gmail.com)) to add you here if you contribute to this project_
* Torstein Vik

Additionally, we thank all the contributors of ([https://github.com/torstein-vik/multfuncs-db](https://github.com/torstein-vik/multfuncs-db)) for the database specification

## Copyright


This framework is and will remain completely open source, under the GNU General Public License version 3+:

    Copyright (C) 2017, Torstein Vik.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    

## Languages/Frameworks

* Implementation: Scala
* Build tool: SBT
* Database: MongoDB
* Database spec.: [https://github.com/torstein-vik/multfuncs-db](https://github.com/torstein-vik/multfuncs-db)

## Folder structure

* /project/ -- Part of SBT

* /src/ -- Source directory, where code is edited.
* /src/main/scala -- Main codebase
* /src/test/scala -- Test for the codebase
