# Components

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
