# alumbra

__alumbra__ is a set of reusable GraphQL components for Clojure conforming to
the data structures given in [alumbra.spec][alumbra-spec]. It also uses these
components to provide an easy-to-use GraphQL infrastructure, allowing you to
get started with minimal effort.

## Quickstart

We start with a GraphQL schema:

```clojure
(def schema
  "type Person { name: String!, friends: [Person!]! }
   type QueryRoot { me: Person! }
   schema { query: QueryRoot }")
```

We define one [claro][claro] resolvable per non-root type:

```clojure
(require '[claro.data :as data])

(defrecord Person [id]
  data/Resolvable
  (resolve! [_ _]
    {:name    (str "Person #" id)
     :friends (map ->Person  (range (inc id) (+ id 3)))}))
```

We declare a map for each root type:

```clojure
(def QueryRoot
  {:me (map->Person {:id 0})})
```

We create the GraphQL handler ...

```clojure
(require '[alumbra.core :as alumbra])

(def app
  (alumbra/handler
    {:schema schema
     :query  QueryRoot}))
```

... and pass it to an HTTP server of our choice:

```clojure
(defonce my-graphql-server
  (aleph.http/start-server #'app {:port 3000}))
```

And the GraphQL-fueled rocket takes off!

```shell
curl -XPOST "http://0:3000" -H'Content-Type: application/json' -d'{
  "query": "{ me { name, friends { name } } }"
}'
```

```json
{"data":{"me":{"name":"Person #0","friends":[{"name":"Person #1"},{"name":"Person #2"}]}}}
```

## Components

__alumbra__ is built upon a series of components that can be used and replaced
individually.

### Specification (clojure.spec)

[![Build Status](https://travis-ci.org/alumbra/alumbra.spec.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.spec)
[![Clojars Project](https://img.shields.io/clojars/v/alumbra/spec.svg)](https://clojars.org/alumbra/spec)

This set of clojure.spec specifications describes the data structures and
interfaces used by the different parts of the alumbra infrastructure. They are
both documentation and means of verification, e.g. in tests.

__[Go to Repository][alumbra-spec]__

[clojure-spec]: http://clojure.org/guides/spec

### Generators (test.check)

[![Build Status](https://travis-ci.org/alumbra/alumbra.generators.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.generators)
[![Clojars Project](https://img.shields.io/clojars/v/alumbra/generators.svg)](https://clojars.org/alumbra/generators)

Mainly used for parser verification, these test.check generators produce
syntactically correct GraphQL queries based on the GraphQL working draft.

__[Go to Repository][alumbra-generators]__

[test-check]: https://github.com/clojure/test.check

### Parser

[![Build Status](https://travis-ci.org/alumbra/alumbra.parser.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.parser)
[![Clojars Project](https://img.shields.io/clojars/v/alumbra/parser.svg)](https://clojars.org/alumbra/parser)

This component is built upon [ANTLR4][antlr], providing a fast parser for both
GraphQL query documents and schemas.

__[Go to Repository][alumbra-parser]__

[antlr]: http://www.antlr.org/

### Analyzer

[![Build Status](https://travis-ci.org/alumbra/alumbra.analyzer.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.analyzer)
[![Clojars Project](https://img.shields.io/clojars/v/alumbra/analyzer.svg)](https://clojars.org/alumbra/analyzer)

The analyzer converts raw GraphQL ASTs to formats suitable for validation and
execution. Most importantly, it can combine a GraphQL schema and a GraphQL query
document to produce a canonical, standalone format that an executor can handle
without further knowledge of the original type system.

__[Go to Repository][alumbra-analyzer]__

### Validator

[![Build Status](https://travis-ci.org/alumbra/alumbra.validator.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.validator)
[![Clojars Project](https://img.shields.io/clojars/v/alumbra/validator.svg)](https://clojars.org/alumbra/validator)

This component is based on [invariant][invariant] to provide validation for
GraphQL query documents according to the GraphQL Working Draft.

[invariant]: https://github.com/xsc/invariant

__[Go to Repository][alumbra-validator]__

### Executor

[![Build Status](https://travis-ci.org/alumbra/alumbra.claro.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.claro)
[![Clojars Project](https://img.shields.io/clojars/v/alumbra/claro.svg)](https://clojars.org/alumbra/claro)

Represent your data as [claro][claro] resolvables and mutations to have them
easily accessible via GraphQL. Additionally, you'll benefit from the flexibility
and optimizations claro provides out-of-the-box, including batching capabilities
and engine extensibility.

__[Go to Repository][alumbra-claro]__

[claro]: https://github.com/xsc/claro

### Ring Handlers

[![Build Status](https://travis-ci.org/alumbra/alumbra.ring.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.ring)
[![Clojars Project](https://img.shields.io/clojars/v/alumbra/ring.svg)](https://clojars.org/alumbra/ring)

This library provides [Ring][ring] handlers for both executing GraphQL queries
and exposing an interactive [GraphiQL][graphiql] environment.

__[Go to Repository][alumbra-ring]__

[ring]: https://github.com/ring-clojure/ring
[graphiql]: https://github.com/graphql/graphiql

[alumbra-spec]: https://github.com/alumbra/alumbra.spec
[alumbra-generators]: https://github.com/alumbra/alumbra.generators
[alumbra-parser]: https://github.com/alumbra/alumbra.parser
[alumbra-analyzer]: https://github.com/alumbra/alumbra.analyzer
[alumbra-validator]: https://github.com/alumbra/alumbra.validator
[alumbra-ring]: https://github.com/alumbra/alumbra.ring
[alumbra-claro]: https://github.com/alumbra/alumbra.claro

## Contributing

Contributions are always welcome. Please take a look at the [Contribution
Guidelines](CONTRIBUTING.md) for a quick overview of how your changes can best
make it to master.

Note that issues for alumbra are tracked centrally within the alumbra
[issue tracker][issues] with single issues being mirrored to the respective
repositories.

[issues]: https://github.com/alumbra/alumbra/issues

## License

```
MIT License

Copyright (c) 2016 Yannick Scherer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
