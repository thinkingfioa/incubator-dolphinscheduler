<template>
  <m-list-construction :title="$t('Datasource')">
    <template slot="conditions">
      <m-conditions @on-conditions="_onConditions">
        <template slot="button-group">
          <x-button type="ghost" size="small" @click="_create('')">{{$t('Create Datasource')}}</x-button>
        </template>
      </m-conditions>
    </template>
    <template slot="content">
      <template v-if="datasourcesList.length">
        <m-list :datasources-list="datasourcesList" :page-no="searchParams.pageNo" :page-size="searchParams.pageSize"></m-list>
        <div class="page-box">
          <x-page :current="parseInt(searchParams.pageNo)" :total="total" :page-size="searchParams.pageSize" show-elevator @on-change="_page"></x-page>
        </div>
      </template>
      <template v-if="!datasourcesList.length">
        <m-no-data></m-no-data>
      </template>
      <m-spin :is-spin="isLoading" :is-left="false">
      </m-spin>
    </template>
  </m-list-construction>
</template>
<script>
  import _ from 'lodash'
  import { mapActions } from 'vuex'
  import mList from './_source/list'
  import mSpin from '@/module/components/spin/spin'
  import mNoData from '@/module/components/noData/noData'
  import mCreateDataSource from './_source/createDataSource'
  import listUrlParamHandle from '@/module/mixin/listUrlParamHandle'
  import mConditions from '@/module/components/conditions/conditions'
  import mListConstruction from '@/module/components/listConstruction/listConstruction'

  export default {
    name: 'datasource-indexP',
    data () {
      return {
        // loading
        isLoading: true,
        // Total number of articles
        total: 20,
        // data sources(List)
        datasourcesList: [],
        searchParams: {
          // Number of pages per page
          pageSize: 10,
          // Number of pages
          pageNo: 1,
          // Search value
          searchVal: ''
        }
      }
    },
    mixins: [listUrlParamHandle],
    props: {},
    methods: {
      ...mapActions('datasource', ['getDatasourcesListP']),
      /**
       * create data source
       */
      _create (item) {
        let self = this
        let modal = this.$modal.dialog({
          closable: false,
          showMask: true,
          escClose: true,
          className: 'v-modal-custom',
          transitionName: 'opacityp',
          render (h) {
            return h(mCreateDataSource, {
              on: {
                onUpdate () {
                  self._debounceGET('false')
                  modal.remove()
                },
                close () {
                  modal.remove()
                }
              },
              props: {
                item: item
              }
            })
          }
        })
      },
      /**
       * page
       */
      _page (val) {
        this.searchParams.pageNo = val
      },
      /**
       * conditions event
       */
      _onConditions (o) {
        this.searchParams = _.assign(this.searchParams, o)
        this.searchParams.pageNo = 1
      },
      /**
       * get data(List)
       */
      _getList (flag) {
        this.isLoading = !flag
        this.getDatasourcesListP(this.searchParams).then(res => {
          this.datasourcesList = []
          this.datasourcesList = res.totalList
          this.total = res.total
          this.isLoading = false
        }).catch(e => {
          this.isLoading = false
        })
      }
    },
    watch: {
      // router
      '$route' (a) {
        // url no params get instance list
        this.searchParams.pageNo = _.isEmpty(a.query) ? 1 : a.query.pageNo
      }
    },
    created () {
    },
    mounted () {
    },
    components: { mList, mConditions, mSpin, mListConstruction, mNoData }
  }
</script>
