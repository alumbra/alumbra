# alumbra

__[Documentation](https://alumbra.github.io/alumbra/)__

__alumbra__ is a set of reusable [GraphQL][graphql] components for Clojure
conforming to the data structures given in [alumbra.spec][alumbra-spec]. It also
uses these components to provide an easy-to-use GraphQL infrastructure, allowing
you to get started with minimal effort.

[![Build Status](https://travis-ci.org/alumbra/alumbra.svg?branch=master)](https://travis-ci.org/alumbra/alumbra)
[![Clojars Project](https://img.shields.io/clojars/v/alumbra.svg)](https://clojars.org/alumbra)

[alumbra-spec]: https://github.com/alumbra/alumbra.spec
[graphql]: http://graphql.org

## Features

- built upon __[claro][claro] resolvables__, allowing you to leverage a powerful
  and customizable data access layer,
- idiomatic Clojure __value and name coercion__ (e.g., for record fields and
  enum values),
- a __fast parser and query validator__,
- compatible with __[GraphiQL][graphiql]__,
- and seamless integration with __[ring][ring]-compatible__ servers,

[claro]: https://github.com/xsc/claro
[ring]: https://github.com/ring-clojure/ring
[graphiql]: https://github.com/graphql/graphiql

## Quickstart

> __Note:__ You might want to familiarize yourself with [claro][claro]'s ideas
> and implementation since that's the source of a lot of alumbra's powers.

```clojure
(require '[alumbra.core :as alumbra]
         '[claro.data :as data])
```

First, we declare our GraphQL schema and implement a `Resolvable` for each
non-root type:

```clojure
(def schema
  "type Person { name: String!, friends: [Person!]! }
   type QueryRoot { person(id: ID!): Person, me: Person! }
   schema { query: QueryRoot }")

(defrecord Person [id]
  data/Resolvable
  (resolve! [_ _]
    {:name    (str "Person #" id)
     :friends (map ->Person  (range (inc id) (+ id 3)))}))
```

Then we declare our `QueryRoot` and instantiate the handler:

```clojure
(def QueryRoot
  {:person (map->Person {})
   :me     (map->Person {:id 0})})

(def app
  (alumbra/handler
    {:schema schema
     :query  QueryRoot}))
```

And this we pass to a Ring-compatible HTTP server of our choice:

```clojure
(defonce my-graphql-server
  (aleph.http/start-server #'app {:port 3000}))
```

Check out our GraphQL endpoint!

```shell
$ curl -XPOST "http://0:3000" -H'Content-Type: application/json' -d'{
  "query": "{ me { name, friends { name } } }"
}'
{"data":{"me":{"name":"Person #0","friends":[{"name":"Person #1"},{"name":"Person #2"}]}}}
```

## Documentation

1. [Component Overview](doc/99-alumbra-components.md)

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

Copyright (c) 2016-2017 Yannick Scherer

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
