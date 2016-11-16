# alumbra

__alumbra__ is a set of reusable GraphQL components for Clojure conforming to
the data structures given in [alumbra.spec][alumbra-spec]. It also uses these
components to provide an easy-to-use GraphQL infrastructure, allowing you to
get started with minimal effort.

## Usage

Coming soon.

## Components

You can include any of alumbra's components individually using the following
libraries:

           | Build Status | Artifact |
:----------|:------------:|:---------:|
[Specification (clojure.spec)][alumbra-spec]  | [![Build Status](https://travis-ci.org/alumbra/alumbra.spec.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.spec) | [![Clojars Project](https://img.shields.io/clojars/v/alumbra/spec.svg)](https://clojars.org/alumbra/spec)
[Generators (test.check)][alumbra-generators] | [![Build Status](https://travis-ci.org/alumbra/alumbra.generators.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.generators) | [![Clojars Project](https://img.shields.io/clojars/v/alumbra/generators.svg)](https://clojars.org/alumbra/generators)
[Parser][alumbra-parser]                      | [![Build Status](https://travis-ci.org/alumbra/alumbra.parser.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.parser) | [![Clojars Project](https://img.shields.io/clojars/v/alumbra/parser.svg)](https://clojars.org/alumbra/parser)
[Analyzer][alumbra-analyzer]                  | [![Build Status](https://travis-ci.org/alumbra/alumbra.analyzer.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.analyzer) | [![Clojars Project](https://img.shields.io/clojars/v/alumbra/analyzer.svg)](https://clojars.org/alumbra/analyzer)
|[Validator][alumbra-validator]               | [![Build Status](https://travis-ci.org/alumbra/alumbra.validator.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.validator) | [![Clojars Project](https://img.shields.io/clojars/v/alumbra/validator.svg)](https://clojars.org/alumbra/validator)
|[Ring Middleware][alumbra-ring]              | [![Build Status](https://travis-ci.org/alumbra/alumbra.ring.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.ring) | [![Clojars Project](https://img.shields.io/clojars/v/alumbra/ring.svg)](https://clojars.org/alumbra/ring)
|[Claro Executor][alumbra-claro]              | [![Build Status](https://travis-ci.org/alumbra/alumbra.claro.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.claro) | [![Clojars Project](https://img.shields.io/clojars/v/alumbra/claro.svg)](https://clojars.org/alumbra/claro)

[alumbra-spec]: https://github.com/alumbra/alumbra.spec
[alumbra-generators]: https://github.com/alumbra/alumbra.generators
[alumbra-parser]: https://github.com/alumbra/alumbra.parser
[alumbra-analyzer]: https://github.com/alumbra/alumbra.analyzer
[alumbra-validator]: https://github.com/alumbra/alumbra.validator
[alumbra-ring]: https://github.com/alumbra/alumbra.ring
[alumbra-claro]: https://github.com/alumbra/alumbra.claro

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
