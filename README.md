##SimpleRxJavaAndRetrofit
If the name isn't obvious, this is a sample app demonstrating how to use RxJava and Retrofit 2.0 together.  I also included Facebook's Stetho and Square's LeakCanary libraries to help with debugging, and to back up my claims.

The app is very simple: I download a list of users from a server and display them using a `RecyclerView`.  I handle screen rotations using a retained `Fragment` to store my `Observable`.  The `Observable` uses the `cache()` operator to save on network requests.  To make this more obvious I added a 3 second delay.

I intentionally left out libraries like Dagger to make the sample easier to follow.

##Opinions
I've seen several projects with the same intentions, however, I consider this implementation to be a best practice.  To me, other implementations are needlessly complicated and fight with the framework, such as with static controllers to handle rotations.

Coding-wise, the one thing I hate about RxJava is the abundance of anonymous classes.  Retrolambda helps to an extent, but you still have huge `Subscriber` classes filling your methods.  I think it looks a lot nicer having these as private inner classes.