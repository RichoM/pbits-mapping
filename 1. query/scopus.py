from pybliometrics.scopus import ScopusSearch, AbstractRetrieval
import pandas as pd

# https://dev.elsevier.com/sc_search_tips.html

#%%
for year in range(2022, 2011, -1):
    #query = 'TITLE ( programming  AND  ( language  OR  environment ) )  AND  ( education )  AND  ( LIMIT-TO ( SUBJAREA ,  "COMP " )  OR  LIMIT-TO ( SUBJAREA ,  "ENGI " )  OR  LIMIT-TO ( SUBJAREA ,  "SOCI " ) ) '
    #query = 'ABS(experimental*) AND ABS(gameplay*)'# AND ABS(mechanics*)'
    #query = 'experimental AND gameplay AND mechanics'
    #query = '(programming environment OR programming language OR programming tool) AND (educational robotics OR physical computing) AND PUBYEAR IS ' + year

    #query = '(TITLE-ABS-KEY(programming environment) OR TITLE-ABS-KEY(programming language) OR TITLE-ABS-KEY(programming tool)) AND (TITLE-ABS-KEY(educational robotics) OR TITLE-ABS-KEY(physical computing)) AND PUBYEAR IS ' + str(year)

    query = '(TITLE-ABS-KEY(programming environment) OR TITLE-ABS-KEY(programming language) OR TITLE-ABS-KEY(programming tool) OR TITLE-ABS-KEY(compiler) OR TITLE-ABS-KEY(interpreter)) AND (TITLE-ABS-KEY(education*) OR TITLE-ABS-KEY(teaching) OR TITLE-ABS-KEY(learning)) AND (TITLE-ABS-KEY(robot*) OR TITLE-ABS-KEY(physical computing) OR TITLE-ABS-KEY(embedded system*)) AND PUBYEAR IS ' + str(year) 
    #%%
    #s=ScopusSearch(query, subscriber=False, verbose=True, download=False)
    #s.get_results_size()
    #%%
    
    # NOTE(Richo): subscriber=True cuando lo ejecutás en la red del CAETI con las credenciales de Scopus
    # (supuestamente te trae toda la info... incluido el abstract)
    s=ScopusSearch(query, subscriber=False)

    print("{}: {}".format(year, s.get_results_size()))
    df = pd.DataFrame(pd.DataFrame(s.results))

    df.to_csv(r"csv/query_7_"+str(year)+".csv")

#%%
