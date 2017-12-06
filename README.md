
# Advanced Database System - 17fall Project

This project implements a small size distributed database, complete with multiversion concurrency control, deadlock detection, replication, and
failure recovery.

## Design

According to the requirement, there are two kinds of transactions.

-  Read-Only Transaction

    - It uses multi-version.
    - It only reads from the copy version when the transaction starts
    - If there are no available copies for read (such as all sites contains variables are down), then the operation will wait

- Read-Write Transaction

    - It uses strict two phase locking. 
        - Acquire locks as the program goes, release locks at end and acquire all locks before releasing any.
        - A variable can't get the exclusive lock when it already has a read lock.
        - A variable can't get any other locks when it already has an exclusive lock.
        - A variable can have multiple read locks/
    - It uses instructors' available copies algorithm
        - Available copies allows writes and commits to just available sites. So if site A is down, its last committed value of x may be different from site B which is up. 
        - Detect deadlocks using cycle detection and abort the youngest transaction in the cycle.
        


### Prerequisites

What things you need to install the software and how to install them

```
Give examples
```

### Installing

A step by step series of examples that tell you have to get a development env running

Say what the step will be

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc
