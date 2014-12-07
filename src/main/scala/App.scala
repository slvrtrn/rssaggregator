import com.twitter.finatra._
import ru.slvr.config.BindingsProvider
import ru.slvr.controllers._

object App extends FinatraServer {
  private implicit val inj = BindingsProvider.getBindings
  register(new Index)
  register(new Auth)
  register(new Feed)
}